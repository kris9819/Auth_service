package com.auth.AuthService.DTO;

import com.auth.AuthService.validation.ValidationEmail;
import com.auth.AuthService.validation.MatchPassword;
import com.auth.AuthService.validation.ValildationPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@MatchPassword
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotNull
    @NotEmpty
    @JsonProperty("login")
    private String login;

    @NotNull
    @NotEmpty
    @JsonProperty("email")
    @ValidationEmail
    private String email;

    @NotNull
    @NotEmpty
    @JsonProperty("password")
    @ValildationPassword
    private String password;

    @NotNull
    @NotEmpty
    @JsonProperty("repeatPassword")
    private String repeatPassword;
}
