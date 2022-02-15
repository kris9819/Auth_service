package com.auth.AuthService.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.auth.AuthService.domain.UserData;
import com.auth.AuthService.domain.VerificationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class UserRepositoryImpl implements UserRepository{

    private DynamoDBMapper getDynamoDBMapper() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        return new DynamoDBMapper(client);
    }

    @Override
    public boolean emailExist(String email) {

        Map<String, AttributeValue> find = new HashMap<String, AttributeValue>();
        find.put(":val1", new AttributeValue().withS(email));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("email = :val1").withExpressionAttributeValues(find);

        List<UserData> scanResult = getDynamoDBMapper().scan(UserData.class, scanExpression);

        if (scanResult.size() != 0)
            return true;

        return false;
    }

    @Override
    public void save(UserData userData) {
        getDynamoDBMapper().save(userData);
    }

    @Override
    public UserData findUserByUUID(String uuid) {

        Map<String, AttributeValue> find = new HashMap<String, AttributeValue>();
        find.put(":val1", new AttributeValue().withS(uuid));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("id = :val1").withExpressionAttributeValues(find);

        List<UserData> scanResult = getDynamoDBMapper().scan(UserData.class, scanExpression);

        return scanResult.get(0);
    }

    @Override
    public UserData findUserByEmail(String email) {
        Map<String, AttributeValue> find = new HashMap<String, AttributeValue>();
        find.put(":val1", new AttributeValue().withS(email));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("email = :val1").withExpressionAttributeValues(find);

        List<UserData> scanResult = getDynamoDBMapper().scan(UserData.class, scanExpression);

        return scanResult.get(0);
    }

    @Override
    public void changeUserPassword(String uuid, String password, PasswordEncoder passwordEncoder) {
        UserData user = findUserByUUID(uuid);
        user.setPassword(passwordEncoder.encode(password));
        getDynamoDBMapper().save(user, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    }
}
