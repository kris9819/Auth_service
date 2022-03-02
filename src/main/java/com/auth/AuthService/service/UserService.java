package com.auth.AuthService.service;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.memorydb.model.UserAlreadyExistsException;
import com.auth.AuthService.DTO.UserDTO;
import com.auth.AuthService.domain.BookList;
import com.auth.AuthService.domain.PasswordResetToken;
import com.auth.AuthService.domain.UserData;
import com.auth.AuthService.domain.VerificationToken;
import com.auth.AuthService.repository.PasswordResetTokenRepository;
import com.auth.AuthService.repository.UserBooksRepository;
import com.auth.AuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserService implements IUserService {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserBooksRepository userBooksRepository;

    @Autowired
    MessageSource messageSource;

    public UserData registerUser (UserDTO userDTO) {

        if (userRepository.emailExist(userDTO.getEmail())){
            throw new UserAlreadyExistsException("Konto z adreesem " + userDTO.getEmail() + " juz istnieje");
        }

        UUID uuid = UUID.randomUUID();
        UserData user = UserData.builder()
                .id(uuid.toString())
                .email(userDTO.getEmail())
                .login(userDTO.getLogin())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build();

        userRepository.save(user);

        return user;
    }

    public void updateUser (UserData userData) {
        userRepository.save(userData);
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

    public void createTokenTable() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "Tokens";

        try {
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("id", KeyType.HASH)),
                    Arrays.asList(new AttributeDefinition("id", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    public void createPasswordResetTokenTable() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "PasswordResetTokens";

        try {
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("id", KeyType.HASH)),
                    Arrays.asList(new AttributeDefinition("id", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    public void createUserBooksTable() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-2"))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "UserBooks";

        try {
            Table table = dynamoDB.createTable(tableName,
                    Arrays.asList(new KeySchemaElement("id", KeyType.HASH)),
                    Arrays.asList(new AttributeDefinition("id", ScalarAttributeType.S)),
                    new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    public UserData findUserByUUID(String uuid) {
        return userRepository.findUserByUUID(uuid);
    }

    @Override
    public UserData findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordResetTokenRepository.findToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    @Override
    public void updatePassword(String uuid, String password) {
        userRepository.changeUserPassword(uuid, password, passwordEncoder);
    }

    @Override
    public void addUserBook(int bookId) {
        userBooksRepository.addUserBook(getLoggedUserId(), bookId);
    }

    @Override
    public BookList getUserBooks() {
        return userBooksRepository.getUserBooksArray(getLoggedUserId());
    }

    public SimpleMailMessage createRegistrationMessage(Locale locale, VerificationToken token, HttpServletRequest request) {
        UserData user = findUserByUUID(token.getToken());
        String appUrl = "http://" + request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
        final String recipientAddress = user.getEmail();
        final String subject = "Reset hasła";
        final String confirmationUrl = appUrl + "/validatePasswordResetToken?token=" + token;
        final String message = messageSource.getMessage("message.ResendToken", null, "Prosze kliknac w ponizszy link, aby potwierdzic rejestracje", locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom("KrisLibraryApp");
        return email;
    }

    public SimpleMailMessage createResetPasswordMessage(Locale locale, UserData user, HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        passwordResetTokenRepository.createToken(user.getId(), token);
        String appUrl = "http://" + request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
        final String recipientAddress = user.getEmail();
        final String subject = "Reset hasła";
        final String confirmationUrl = appUrl + "/validatePasswordResetToken?token=" + token;
        final String message = messageSource.getMessage("message.ResendToken", null, "Prosze kliknac w ponizszy link, aby potwierdzic rejestracje", locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + " \r\n" + confirmationUrl);
        email.setFrom("KrisLibraryApp");
        return email;
    }

    private String getLoggedUserId() {
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }

        return findByEmail(username).getId();
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }


}
