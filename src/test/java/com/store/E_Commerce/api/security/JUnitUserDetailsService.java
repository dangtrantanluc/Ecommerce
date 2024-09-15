package com.store.E_Commerce.api.security;

import com.store.E_Commerce.model.LocalUser;
import com.store.E_Commerce.model.dao.LocalUserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@Primary
public class JUnitUserDetailsService implements UserDetailsService {
    @Autowired
    private LocalUserDAO localUserDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(username);
        if (opUser.isPresent()){
            return opUser.get();
        }
        return null;
    }
}
