package com.store.E_Commerce.model.dao;

import com.store.E_Commerce.model.Address;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface AddressDAO extends ListCrudRepository<Address, Long> {
    List<Address> findByUser_id(Long id);
}
