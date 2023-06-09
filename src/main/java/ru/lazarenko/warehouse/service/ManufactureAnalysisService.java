package ru.lazarenko.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lazarenko.warehouse.entity.ManufactureAnalysis;
import ru.lazarenko.warehouse.entity.OperationHistory;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Storage;
import ru.lazarenko.warehouse.model.TypeNeedChange;
import ru.lazarenko.warehouse.model.TypeOperation;
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
    private final Duration duration = Duration.ofMinutes(3); // Duration.ofDays(7)

    @Scheduled(fixedDelay = 3, timeUnit = TimeUnit.MINUTES) // @Scheduled(fixedDelay = 7, timeUnit = TimeUnit.DAYS)
    @Transactional
    public void createNotice() {
        /*
        if shipments > 50% of deliveries, then we increase deliveries
        if shipments are < 20% of deliveries, then we reduce deliveries
         */
        makeDataSlice();
    }

    private void makeDataSlice() {
        LocalDateTime time = LocalDateTime.now().minus(duration);

        List<OperationHistory> data = operationHistoryRepository.findAllByDateIsAfter(time);

        List<ManufactureAnalysis> result = new ArrayList<>();

        int countLoading;
        int countShipment;
        for (int i = 0; i < data.size(); i++) {
            Product currentProduct = data.get(i).getProduct();
            Storage currentStorage = data.get(i).getStorage();
            countLoading = data.stream()
                    .filter(el -> el.getProduct().getId().equals(currentProduct.getId())
                            && el.getStorage().getId().equals(currentStorage.getId())
                            && el.getOperation().equals(TypeOperation.LOADING))
                    .mapToInt(OperationHistory::getCount).sum();

            countShipment = data.stream()
                    .filter(el -> el.getProduct().getId().equals(currentProduct.getId())
                            && el.getStorage().getId().equals(currentStorage.getId())
                            && el.getOperation().equals(TypeOperation.SHIPMENT))
                    .mapToInt(OperationHistory::getCount).sum();

            if (countShipment != 0 && (countLoading / countShipment * 100) > 50) {
                ManufactureAnalysis notice = ManufactureAnalysis.builder()
                        .product(currentProduct)
                        .storage(currentStorage)
                        .typeOperation(TypeOperation.LOADING)
                        .typeNeedChange(TypeNeedChange.DECREASE)
                        .build();
                result.add(notice);
                continue;
            }

            if (countLoading != 0 && (countShipment / countLoading * 100) < 30) {
                ManufactureAnalysis notice = ManufactureAnalysis.builder()
                        .product(currentProduct)
                        .storage(currentStorage)
                        .typeOperation(TypeOperation.LOADING)
                        .typeNeedChange(TypeNeedChange.INCREASE)
                        .build();
                result.add(notice);
            }
        }

        manufactureAnalysisRepository.saveAll(result);
    }
}


