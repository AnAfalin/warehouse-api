package ru.lazarenko.warehouse.service.mapper;

import org.mapstruct.Mapper;
import ru.lazarenko.warehouse.dto.product.ProductDto;
import ru.lazarenko.warehouse.entity.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toProduct(ProductDto dto);

    ProductDto toProductDto(Product product);

    List<ProductDto> toProductDtoList(List<Product> products);
}
