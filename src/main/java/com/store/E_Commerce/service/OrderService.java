package com.store.E_Commerce.service;

import com.store.E_Commerce.model.LocalUser;
import com.store.E_Commerce.model.WebOrder;
import com.store.E_Commerce.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private WebOrderDAO webOrderDAO;
    public OrderService(WebOrderDAO webOrderDAO){
        this.webOrderDAO = webOrderDAO;
    }
    public List<WebOrder> getOrders(LocalUser user){
        return webOrderDAO.findByUser(user);
    }
}
