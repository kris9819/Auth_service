package com.auth.AuthService.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitListenerService implements MessageListener {
    @Override
    public void onMessage(Message message) {
        String msg = new String(message.getBody());
        if (msg.equals("uuid")) {
            try {
                RabbitTestService.sendMsg();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        else
            System.out.println(new String(message.getBody()));
    }
}
