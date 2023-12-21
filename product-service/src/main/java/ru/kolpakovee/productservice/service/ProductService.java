package ru.kolpakovee.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kolpakovee.productservice.dto.ProductRequest;
import ru.kolpakovee.productservice.dto.ProductResponse;
import ru.kolpakovee.productservice.model.Product;
import ru.kolpakovee.productservice.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();

        productRepository.save(product);
        log.info("Product with id {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        var products = productRepository.findAll();

        return products.stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    private ProductResponse mapToProductResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
