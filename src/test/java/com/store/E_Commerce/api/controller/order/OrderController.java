package com.store.E_Commerce.api.controller.order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.E_Commerce.model.WebOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderController {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testUserAuthenticationOrderList() throws Exception {
        mvc.perform(get("/order")).andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
            String json = result.getResponse().getContentAsString();
            List<WebOrder>orders = new ObjectMapper().readValue(json, new TypeReference<List<WebOrder>>() {});
            for( WebOrder order: orders){
                Assertions.assertEquals("tanluc",order.getUser().getUsername(),"Order list should only be orders belonging to the user");
            }
        });
    }

    @Test
    public void testUnauthenticationOrderList()throws Exception{
        mvc.perform(get("/order")).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }
}
