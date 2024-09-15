package com.store.E_Commerce.api.security;

import com.store.E_Commerce.model.LocalUser;
import com.store.E_Commerce.model.dao.LocalUserDAO;
import com.store.E_Commerce.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JWTRequestFilterTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserDAO localUserDAO;
    private static final String AUTHENTICATED_PATH = "/auth/me";

    @Test
    public void testUnauthenticationRequest() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testBadToken() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "BadTokenIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer BadTokenIsNotValid"))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    public void testUnverifiedUser() throws Exception {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("tanluc1").get();
        String token = jwtService.generateJWT(user);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization","Bearer" + token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

    }

    @Test
    public void testSuccessful() throws Exception {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("tanluc").get();
        String token = jwtService.generateJWT(user);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization ","Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
        //expected 200 OK
        //actual 403 FORBIDENT
    }
}

