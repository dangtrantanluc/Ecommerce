package com.store.E_Commerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.store.E_Commerce.model.LocalUser;
import com.store.E_Commerce.model.dao.LocalUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JWTServiceTest {
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserDAO localUserDAO;
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Test
    public void testVerifiedTokenNotUsableForLogin(){
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("tanluc").get();
        String token = jwtService.generateVerificationJWT(user);
        Assertions.assertNull(jwtService.getUsername(token),"VerifiedToken shoud not contain username");

    }

    @Test
    public void testAuthTokenReturnsUsername(){
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("tanluc").get();
        String token = jwtService.generateJWT(user);
        Assertions.assertEquals(user.getUsername(),jwtService.getUsername(token),"Token for auth should contain users username");
    }

    @Test
    public void testJWTNotGeneratedByUs(){
        String token  = JWT.create().withClaim("USERNAME","tanluc").sign(Algorithm.HMAC256("NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class,()-> jwtService.getUsername(token));
    }

    @Test
    public void testResetPasswordJWTNotGeneratedByUs() {
        String token =
                JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com").sign(Algorithm.HMAC256(
                        "NotTheRealSecret"));
        Assertions.assertThrows(SignatureVerificationException.class,
                () -> jwtService.getResetPasswordEmail(token));
    }


    @Test
    public void testResetPasswordJWTCorrectlySignedNoIssuer() {
        String token =
                JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com")
                        .sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class,
                () -> jwtService.getResetPasswordEmail(token));
    }

    @Test
    public void testPasswordResetToken() {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("tanluc").get();
        String token = jwtService.generatePasswordReset(user);
        Assertions.assertEquals(user.getEmail(),
                jwtService.getResetPasswordEmail(token), "Email should match inside " +
                        "JWT.");
    }
}
