package ru.lazarenko.warehouse.scheduled;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.entity.ManufactureAnalysis;
import ru.lazarenko.warehouse.entity.OperationHistory;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.model.ChangeType;
import ru.lazarenko.warehouse.model.OperationType;
import ru.lazarenko.warehouse.repository.ManufactureAnalysisRepository;
import ru.lazarenko.warehouse.repository.OperationHistoryRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ManufactureAnalysisService {
    private final ManufactureAnalysisRepository manufactureAnalysisRepository;

    private final OperationHistoryRepository operationHistoryRepository;

    @Value("${my.scheduler.period-report-analyses}")
    private Long periodReportAnalyses;

    @Scheduled(fixedDelayString = "${my.scheduler.interval}", timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void createNotice() {
        /*
        if shipments > 50% of deliveries, then we increase deliveries
        if shipments are < 20% of deliveries, then we reduce deliveries
         */
        List<OperationHistory> sliceData = makeDataSlice();

        List<ManufactureAnalysis> result = makeAnalysis(sliceData);

        manufactureAnalysisRepository.saveAll(result);
    }

    private List<OperationHistory> makeDataSlice() {
        LocalDateTime time = LocalDateTime.now().minus(Duration.ofMinutes(periodReportAnalyses));
        return operationHistoryRepository.findAllByDateIsAfter(time);
    }

    private List<ManufactureAnalysis> makeAnalysis(List<OperationHistory> data) {
        List<ManufactureAnalysis> result = new ArrayList<>();

        int countLoading;
        int countShipment;

        for (int i = 0; i < data.size(); i++) {
            Product currentProduct = data.get(i).getProduct();
            Storage currentStorage = data.get(i).getStorage();

            countLoading = calculateCountLoadingByProductAndStorage(currentProduct, currentStorage, data);
            countShipment = calculateCountShipmentByProductAndStorage(currentProduct, currentStorage, data);

            if (countShipment != 0 && (countLoading / countShipment * 100) > 50) {
                writeNoticeIncrease(currentProduct, currentStorage, result);
                continue;
            }

            if (countLoading != 0 && (countShipment / countLoading * 100) < 30) {
                writeNoticeDecrease(currentProduct, currentStorage, result);
            }
        }

        return result;
    }

    private int calculateCountLoadingByProductAndStorage(Product product, Storage storage, List<OperationHistory> data) {
        return data.stream()
                .filter(el -> el.getProduct().getId().equals(product.getId())
                        && el.getStorage().getId().equals(storage.getId())
                        && el.getOperation().equals(OperationType.LOADING))
                .mapToInt(OperationHistory::getCount).sum();
    }

    private int calculateCountShipmentByProductAndStorage(Product product, Storage storage, List<OperationHistory> data) {
        return data.stream()
                .filter(el -> el.getProduct().getId().equals(product.getId())
                        && el.getStorage().getId().equals(storage.getId())
                        && el.getOperation().equals(OperationType.SHIPMENT))
                .mapToInt(OperationHistory::getCount).sum();
    }


    private void writeNoticeIncrease(Product product, Storage storage, List<ManufactureAnalysis> result) {
        ManufactureAnalysis notice = ManufactureAnalysis.builder()
                .product(product)
                .storage(storage)
                .operation(OperationType.LOADING)
                .changeType(ChangeType.DECREASE)
                .build();
        result.add(notice);
    }

    private void writeNoticeDecrease(Product product, Storage storage, List<ManufactureAnalysis> result) {
        ManufactureAnalysis notice = ManufactureAnalysis.builder()
                .product(product)
                .storage(storage)
                .operation(OperationType.LOADING)
                .changeType(ChangeType.INCREASE)
                .build();
        result.add(notice);
    }
}