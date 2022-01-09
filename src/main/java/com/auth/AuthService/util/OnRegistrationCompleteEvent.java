package com.auth.AuthService.util;

import com.auth.AuthService.domain.UserData;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Data
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private UserData userData;

    public OnRegistrationCompleteEvent(String appUrl, Locale locale, UserData userData) {
        super(userData);

        this.appUrl = appUrl;
        this.locale = locale;
        this.userData = userData;
    }
}
