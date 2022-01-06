package com.auth.AuthService.service;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.memorydb.model.UserAlreadyExistsException;
import com.auth.AuthService.DTO.UserDTO;
import com.auth.AuthService.domain.UserData;
import com.auth.AuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepository;

    public UserData registerUser (UserDTO userDTO) {

        if (userRepository.emailExist(userDTO.getEmail())){
            throw new UserAlreadyExistsException("Konto z adreesem " + userDTO.getEmail() + " juz istnieje");
        }

        UUID uuid = UUID.randomUUID();
        UserData user = UserData.builder()
                .id(uuid.toString())
                .email(userDTO.getEmail())
                .login(userDTO.getLogin())
                .password(userDTO.getPassword()).build();

        userRepository.save(user);

        return user;
    }

    public void test() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "Users";

        try {
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("id", KeyType.HASH),
                            new KeySchemaElement("email", KeyType.RANGE)),
                    Arrays.asList(new AttributeDefinition("id", ScalarAttributeType.S),
                            new AttributeDefinition("email", ScalarAttributeType.S)),
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
            userData.setEmail(outcome.getString("email"));

            System.out.println(userData.toString());

        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + login + " " + password);
            System.err.println(e.getMessage());
        }
    }

}
