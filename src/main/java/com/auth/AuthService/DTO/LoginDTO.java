package com.auth.AuthService.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotNull
    @NotEmpty
    @JsonProperty("email")
    private String email;

    @NotNull
    @NotEmpty
    @JsonProperty("password")
    private String password;
}
