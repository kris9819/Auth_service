package com.auth.AuthService.service;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Service
public class RabbitTestService {

    public void sendMsg() throws NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername("KrzychuDzik");
        factory.setPassword("TestPasswd123");

        factory.setHost("b-c2d9196a-9a6a-492e-97e2-6fa4a8194d98.mq.us-east-2.amazonaws.com");
        factory.setPort(5671);
        factory.useSslProtocol();

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        byte[] message = "random msg".getBytes();
        channel.basicPublish("FirstExchange", "FirstQueue",
                new AMQP.BasicProperties().builder()
                        .contentType("text/plain")
                        .userId("KrzychuDzik")
                        .build(),
                        message);

    }
}
