package com.auth.AuthService;

import com.rabbitmq.client.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootTest
class AuthServiceApplicationTests {

	@Test
	void contextLoads() throws NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();

		factory.setUsername("***");
		factory.setPassword("***");

		factory.setHost("b-c2d9196a-9a6a-492e-97e2-6fa4a8194d98.mq.us-east-2.amazonaws.com");
		factory.setPort(5671);
		factory.useSslProtocol();

		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		byte[] message = "random msg".getBytes();
		channel.basicPublish("FirstExchange", "FirstQueue",
				new AMQP.BasicProperties().builder()
						.contentType("text/plain")
						.userId("***")
						.build(),
				message);

		List<String> messages = new ArrayList<>();

		channel.basicConsume("FirstQueue", false, "***",
				new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
						String routingKey = envelope.getRoutingKey();
						String contentType = properties.getContentType();
						long deliveryTag = envelope.getDeliveryTag();
						messages.add(new String(body, StandardCharsets.UTF_8));
						channel.basicAck(deliveryTag, false);
					}
				});

		TimeUnit.MILLISECONDS.sleep(200L);
		Assert.assertTrue("Queue did not receive message", messages.size() > 0);
	}
}
