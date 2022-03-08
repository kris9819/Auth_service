package com.auth.AuthService.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.auth.AuthService.domain.PasswordResetToken;
import com.auth.AuthService.domain.VerificationToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {
    private DynamoDBMapper getDynamoDBMapper() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        return new DynamoDBMapper(client);
    }

    @Override
    public void createToken(String uuid, String token) {
        final PasswordResetToken passwordResetToken = new PasswordResetToken(token, uuid);
        getDynamoDBMapper().save(passwordResetToken);
    }

    @Override
    public PasswordResetToken findToken(String token) {
        Map<String, AttributeValue> find = new HashMap<String, AttributeValue>();
        find.put(":val1", new AttributeValue().withS(token));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("passwordResetToken = :val1").withExpressionAttributeValues(find);

        List<PasswordResetToken> scanResult = getDynamoDBMapper().scan(PasswordResetToken.class, scanExpression);

        if (scanResult.size() != 0)
            return scanResult.get(0);

        return null;
    }
}
