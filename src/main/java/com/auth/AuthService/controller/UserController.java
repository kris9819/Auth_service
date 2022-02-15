package com.auth.AuthService.controller;

import com.amazonaws.services.memorydb.model.UserAlreadyExistsException;
import com.auth.AuthService.DTO.LoginDTO;
import com.auth.AuthService.DTO.PasswordDTO;
import com.auth.AuthService.DTO.UserDTO;
import com.auth.AuthService.domain.*;
import com.auth.AuthService.repository.PasswordResetTokenRepository;
import com.auth.AuthService.repository.TokenRepository;
import com.auth.AuthService.service.UserService;
import com.auth.AuthService.util.GenericResponse;
import com.auth.AuthService.util.OnRegistrationCompleteEvent;
import com.auth.AuthService.util.error.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    UserService userService;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    MessageSource messageSource;

    @PostMapping("/register")
    public GenericResponse registerUser(@Valid @RequestBody final UserDTO userDTO,
                                        HttpServletRequest request) {
        try {
            UserData user = userService.registerUser(userDTO);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(appUrl, request.getLocale(), user));

        } catch (UserAlreadyExistsException exception) {
            return new GenericResponse("Uzytkownik o takim emailu juz istnieje");
        }
        return new GenericResponse("Success");
    }

    @GetMapping("/registrationConfirm")
    public GenericResponse confirmRegistratrion(WebRequest request, @RequestParam("token") String token) {
        Locale locale = request.getLocale();

        VerificationToken verificationToken = tokenRepository.findToken(token);
        if (verificationToken == null) {
            return new GenericResponse("Niepoprawny token");
        }

        String uuid = verificationToken.getUserUUID();
        UserData user = userService.findUserByUUID(uuid);
        Calendar calendar = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0) {
            return new GenericResponse("Token wygasl");
        }

        user.setEnabled(true);
        userService.updateUser(user);

        return new GenericResponse("Success");
    }


    @GetMapping("/resendRegistrationToken")
    public GenericResponse resendRegistrationToken(HttpServletRequest request,
                                                   @RequestParam("token") String existingToken) {
        VerificationToken token = tokenRepository.genNewToken(existingToken);

        UserData user = userService.findUserByUUID(token.getUserUUID());
        String appUrl = "http://" + request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
        SimpleMailMessage simpleMailMessage  = constructResendVerificationTokenEmail(appUrl, request.getLocale(), token, user);
        javaMailSender.send(simpleMailMessage);

        return new GenericResponse("Token wyslany ponownie");
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginDTO loginDTO) {
    }

    @GetMapping("/create")
    public void test() {
        userService.test();
    }

    @GetMapping("/createTokens")
    public void createTokens() {
        userService.createTokenTable();
    }

    @GetMapping("/createPasswordTokens")
    public void createPasswordTokens() {
        userService.createPasswordResetTokenTable();
    }

    @GetMapping("createUserBooks")
    public void createUserBooks() {
        userService.createUserBooksTable();
    }

    @PostMapping("/resetPassword")
    public GenericResponse resetPassword(HttpServletRequest request,
                                         @RequestParam("email") String userEmail) {
        UserData user = userService.findByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException();
        }
        String token = UUID.randomUUID().toString();
        passwordResetTokenRepository.createToken(user.getId(), token);
        String appUrl = "http://" + request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
        javaMailSender.send(constructPasswordTokenEmail(appUrl, request.getLocale(), token, user));
        return new GenericResponse("Email wyslany");
    }

    @GetMapping("/validatePasswordResetToken")
    public boolean validatePasswordResetToken(Locale locale, @RequestParam("token") String token) {
        String result = userService.validatePasswordResetToken(token);
        if(result == null) {
            return true;
        } else {
            return false;
        }
    }

    @PostMapping("/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid @RequestBody PasswordDTO passwordDTO) {
        String result = userService.validatePasswordResetToken(passwordDTO.getToken());

        if (result != null) {
            return new GenericResponse("Nie znaleziono takiego tokena");
        }

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findToken(passwordDTO.getToken());
        UserData user = userService.findUserByUUID(passwordResetToken.getUserUUID());
        if (user == null) {
            return new GenericResponse("Nie znaleziono uzytkownika");
        } else {
            userService.updatePassword(user.getId(), passwordDTO.getNewPassword());
            return new GenericResponse("Success");
        }
    }

    @GetMapping("/addUserBook")
    public GenericResponse addUserBook(@RequestParam("bookId") Integer bookId) {
        userService.addUserBook(bookId);
        return new GenericResponse("Success");
    }

    @GetMapping("/getUserBooks")
    public BookList getUserBooks() {
        return userService.getUserBooks();
    }

    private SimpleMailMessage constructResendVerificationTokenEmail
            (String contextPath, Locale locale, VerificationToken newToken, UserData user) {
        final String recipientAddress = user.getEmail();
        final String subject = "Ponowne potwierdzenie rejestracji";
        final String confirmationUrl = contextPath + "/registrationConfirm?token=" + newToken.getToken();
        final String message = messageSource.getMessage("message.ResendToken", null, "Prosze kliknac w ponizszy link, aby potwierdzic rejestracje", locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom("KrisLibraryApp");
        return email;
    }

    private SimpleMailMessage constructPasswordTokenEmail
            (String contextPath, Locale locale, String token, UserData user) {
        final String recipientAddress = user.getEmail();
        final String subject = "Reset hasła";
        final String confirmationUrl = contextPath + "/validatePasswordResetToken?token=" + token;
        final String message = messageSource.getMessage("message.ResendToken", null, "Prosze kliknac w ponizszy link, aby potwierdzic rejestracje", locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom("KrisLibraryApp");
        return email;
    }
}
