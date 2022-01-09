package com.auth.AuthService.config;

import com.auth.AuthService.domain.UserData;
import com.auth.AuthService.util.OnRegistrationCompleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

//    @Autowired
//    stworzyc repository tokenow

    @Autowired
    MessageSource messageSource;

    @Autowired
    JavaMailSender javaMailSender;
    //ustawic konfiguracje w pliku properties

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {

    }

    private void sendActivateLink(OnRegistrationCompleteEvent event) {
        final UserData user = event.getUserData();
        final String token = UUID.randomUUID().toString();
        //utworzyc token w bazie

        final SimpleMailMessage email = constructEmailMessage(event, user, token);
        javaMailSender.send(email);
    }

    private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final UserData user, final String token) {
        final String recipientAddress = user.getEmail();
        final String subject = "Potwierdzenie rejestracji";
        final String confirmationUrl = event.getAppUrl() + "/registrationConfirm.html?token=" + token;
        final String message = messageSource.getMessage("message.regSuccLink", null, "Prosze kliknac w ponizszy link, aby potwierdzic rejestracje", event.getLocale());
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom("test");
        return email;
    }
}
