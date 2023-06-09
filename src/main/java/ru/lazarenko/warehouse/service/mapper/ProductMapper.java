package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.lazarenko.warehouse.dto.CategoryDto;
import ru.lazarenko.warehouse.dto.ProductDto;
import ru.lazarenko.warehouse.dto.RegionDto;
import ru.lazarenko.warehouse.entity.Category;
import ru.lazarenko.warehouse.entity.Product;
import ru.lazarenko.warehouse.entity.Region;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductDto dto);

    ProductDto toProductDto(Product product);

    List<ProductDto> toProductDtoList(List<Product> products);

    void update(@MappingTarget Product product, ProductDto productDto);

}
