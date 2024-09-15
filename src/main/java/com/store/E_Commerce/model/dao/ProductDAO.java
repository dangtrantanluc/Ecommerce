package com.store.E_Commerce.model.dao;

import com.store.E_Commerce.model.LocalUser;
import com.store.E_Commerce.model.Product;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface ProductDAO extends ListCrudRepository<Product, Long> {
}
