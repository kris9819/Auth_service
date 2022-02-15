package com.auth.AuthService.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.auth.AuthService.domain.UserData;
import com.auth.AuthService.domain.VerificationToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenRepositoryImpl implements TokenRepository {

    private DynamoDBMapper getDynamoDBMapper() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        return new DynamoDBMapper(client);
    }

    @Override
    public void createToken(String uuid, String token) {
        final VerificationToken verificationToken = new VerificationToken(token, uuid);
        getDynamoDBMapper().save(verificationToken);
    }

    @Override
    public VerificationToken findToken(String token) {
        Map<String, AttributeValue> find = new HashMap<String, AttributeValue>();
        find.put(":val1", new AttributeValue().withS(token));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("validationToken = :val1").withExpressionAttributeValues(find);

        List<VerificationToken> scanResult = getDynamoDBMapper().scan(VerificationToken.class, scanExpression);

        if (scanResult.size() != 0)
            return scanResult.get(0);

        return null;
    }

    @Override
    public VerificationToken genNewToken(String token) {
        VerificationToken verificationToken = findToken(token);
        verificationToken.updateToken(UUID.randomUUID().toString());
        getDynamoDBMapper().save(verificationToken, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
        return verificationToken;
    }
}
