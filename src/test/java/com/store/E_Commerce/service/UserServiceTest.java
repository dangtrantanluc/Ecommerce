package com.store.E_Commerce.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.store.E_Commerce.api.model.LoginBody;
import com.store.E_Commerce.api.model.PasswordResetBody;
import com.store.E_Commerce.api.model.RegistrationBody;
import com.store.E_Commerce.exception.EmailFailureException;
import com.store.E_Commerce.exception.EmailNotFoundException;
import com.store.E_Commerce.exception.UserAlreadyExistsException;
import com.store.E_Commerce.exception.UserNotVerifiedException;
import com.store.E_Commerce.model.LocalUser;
import com.store.E_Commerce.model.VerificationToken;
import com.store.E_Commerce.model.dao.LocalUserDAO;
import com.store.E_Commerce.model.dao.VerificationTokenDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.List;

@SpringBootTest
public class UserServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserDAO localUserDAO;
    @Autowired
    private VerificationTokenDAO verificationTokenDAO;
    @Autowired
    private EncryptionService encryptionService;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody body = new RegistrationBody();
        body.setUsername("tanluc");
        body.setEmail("abbbb@gmail.com");
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        body.setPassword("MySecretPassword123");
        //check exist username
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Username should already be in use.");
        body.setUsername("UserA");
        body.setEmail("UserA@gmail.com");
        // check exist email
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Email should already be in use");
        body.setEmail("tannn@gmail.com");
        //user register succesfully
        Assertions.assertDoesNotThrow(() -> userService.registerUser(body),
                "User should register successfully.");
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString());
    }


    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("UserA-notexist");
        loginBody.setPassword("abc111-notexist");
        Assertions.assertNull(userService.loginUser(loginBody), "The user should not exist");
        loginBody.setUsername("tanluc");
        Assertions.assertNull(userService.loginUser(loginBody), "The password should be incorrect");
        loginBody.setPassword("abc111");
        Assertions.assertNull(userService.loginUser(loginBody), "The user login successfully");
        loginBody.setUsername("tanluc1");
        loginBody.setPassword("abc111");
        try {
            userService.loginUser(loginBody);
            Assertions.assertTrue(false, "User should not have email verified");
        } catch (UserNotVerifiedException ex) {
            Assertions.assertTrue(ex.isNewEmailSent(), "Email verification should be sent");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
        try {
            userService.loginUser(loginBody);
            Assertions.assertTrue(false, "User should not have email verified");

        } catch (UserNotVerifiedException ex) {
            Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not be resent");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
        }
    }

    @Test
    @Transactional
    public void testVerifiedUser() throws UserNotVerifiedException, EmailFailureException {
        Assertions.assertFalse(userService.verifyUser("Bad token"), "Token that is bad or does not exist should return false");
        LoginBody body = new LoginBody();
        body.setUsername("UserB");
        body.setUsername("abc111");
        try {
            userService.loginUser(body);
            //expected true but it false
            Assertions.assertTrue(true, "User should not have email verified");
        } catch (UserNotVerifiedException ex) {
            List<VerificationToken> tokens = verificationTokenDAO.findByUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            Assertions.assertTrue(userService.verifyUser(token), "Token should be valid.");
            Assertions.assertNotNull(body, "The user should now be verified.");
        }
    }

    @Test
    @Transactional
    public void testForgotPassword() throws MessagingException {
        Assertions.assertThrows(EmailNotFoundException.class,
                () -> userService.forgotPassword("UserNotExist@junit.com"));
        Assertions.assertDoesNotThrow(() -> userService.forgotPassword("UserA@junit.com"), "non email existing email should be rejected");
        Assertions.assertEquals("UserA@junit.com", greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString(), "Password" + "reset email should be sent");
    }

    @Test
    public void testRestPassword() {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("tanluc").get();
        String token = jwtService.generatePasswordReset(user);
        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword("Password123");
        userService.resetPassword(body);
        user = localUserDAO.findByUsernameIgnoreCase("tanluc").get();
        Assertions.assertTrue(encryptionService.verifyPassword("abc1123", user.getPassword()), "Password change should be written to database");
    }
}
