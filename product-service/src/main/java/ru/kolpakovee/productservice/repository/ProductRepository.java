package ru.kolpakovee.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.kolpakovee.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
