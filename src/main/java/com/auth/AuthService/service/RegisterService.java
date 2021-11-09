package com.auth.AuthService.service;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.auth.AuthService.domain.UserData;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RegisterService {

    public void registerUser (UserData userData) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        UUID uuid = UUID.randomUUID();
        String tableName = "Users";

        Map<String,AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put("id", new AttributeValue().withS(String.valueOf(uuid)));
        attributeValues.put("login", new AttributeValue().withS(userData.getLogin()));
        attributeValues.put("email", new AttributeValue().withS(userData.getMail()));
        attributeValues.put("password", new AttributeValue().withS(userData.getPassword()));

        PutItemRequest putItemRequest = new PutItemRequest()
                .withTableName(tableName)
                .withItem(attributeValues);

        PutItemResult putItemResult = client.putItem(putItemRequest);
    }

    public void test() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "Users";

        try {
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("login", KeyType.HASH),
                            new KeySchemaElement("password", KeyType.RANGE)),
                    Arrays.asList(new AttributeDefinition("login", ScalarAttributeType.S),
                            new AttributeDefinition("password", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    public void findUser(UserData userData) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("Users");

        String login = userData.getLogin();
        String password = userData.getPassword();

        GetItemSpec spec = new GetItemSpec().withPrimaryKey("login", login, "password", password);

        try {
            Item outcome = table.getItem(spec);

            userData.setId(outcome.getString("id"));
            userData.setMail(outcome.getString("email"));

            System.out.println(userData.toString());

        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + login + " " + password);
            System.err.println(e.getMessage());
        }
    }

}
