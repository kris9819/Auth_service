package com.auth.AuthService.DTO;

import com.auth.AuthService.validation.ValildationPassword;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {

    @NotNull
    @NotEmpty
    @JsonProperty("oldPassword")
    private String oldPassword;

    @NotNull
    @NotEmpty
    @JsonProperty("token")
    private String token;

    @NotNull
    @NotEmpty
    @JsonProperty("newPassword")
    @ValildationPassword
    private String newPassword;
}
