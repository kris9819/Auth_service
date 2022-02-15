package com.auth.AuthService.repository;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.auth.AuthService.domain.BookList;
import com.auth.AuthService.domain.UserBooks;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserBooksRepositoryImpl implements UserBooksRepository {

    private DynamoDBMapper getDynamoDBMapper() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        return new DynamoDBMapper(client);
    }

    @Override
    public BookList getUserBooksArray(String uuid) {
        Map<String, AttributeValue> find = new HashMap<String, AttributeValue>();
        find.put(":val1", new AttributeValue().withS(uuid));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("userUUID = :val1").withExpressionAttributeValues(find);

        List<UserBooks> scanResult = getDynamoDBMapper().scan(UserBooks.class, scanExpression);

        List<Integer> booksList = scanResult.stream().map(UserBooks ->  UserBooks.getBookId()).collect(Collectors.toList());
        if (scanResult.size() != 0)
            return BookList.builder()
                    .bookList(booksList)
                    .build();

        return null;
    }

    @Override
    public void addUserBook(String uuid, int bookId) {
        final UserBooks userBooks = UserBooks.builder()
                .uuid(uuid)
                .bookId(bookId)
                .comment("")
                .grade(-1)
                .status("").build();
        getDynamoDBMapper().save(userBooks);
    }
}
