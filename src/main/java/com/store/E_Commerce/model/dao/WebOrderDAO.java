package com.store.E_Commerce.model.dao;

import com.store.E_Commerce.model.LocalUser;
import com.store.E_Commerce.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);
}
