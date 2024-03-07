package com.java.service;

import com.java.dto.ProductEvent;
import com.java.entity.Product;
import com.java.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @KafkaListener(topics = "topic-cqrs-product-event", groupId = "topic-cqrs-product-event-group")
    public void processProductEvents(ProductEvent productEvent) {
        Product product = productEvent.getProduct();
        if (productEvent.getEventName().equals("addProduct")) {
            productRepository.save(product);
        }
        if (productEvent.getEventName().equals("updateProduct")) {
            Product existingProduct = productRepository.findById(product.getId()).get();
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setDescription(product.getDescription());
            productRepository.save(existingProduct);
        }
    }

}
