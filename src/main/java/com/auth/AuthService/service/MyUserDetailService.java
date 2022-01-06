//package com.auth.AuthService.service;
//
//import com.amazonaws.client.builder.AwsClientBuilder;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
//import com.amazonaws.services.dynamodbv2.document.DynamoDB;
//import com.amazonaws.services.dynamodbv2.document.ItemCollection;
//import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
//import com.amazonaws.services.dynamodbv2.document.Table;
//import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
//import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@Transactional
//public class MyUserDetailService implements UserDetailsService {
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        if ()
//    }
//
//    private boolean emailExist(String email) {
//        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
//                .build();
//
//        DynamoDB dynamoDB = new DynamoDB(client);
//
//        Table table = dynamoDB.getTable("Users");
//
//        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("id = :v_id")
//                .withValueMap(new ValueMap().withString(":v_id", email));
//
//        ItemCollection<QueryOutcome> items = table.query(querySpec);
//
//        return items != null;
//    }
//}
