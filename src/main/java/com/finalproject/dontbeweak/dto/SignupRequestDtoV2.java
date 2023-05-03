package com.finalproject.dontbeweak.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDtoV2 {


    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}$", message = "아이디는 4~10자여야 합니다.")
    private String username;

    @NotBlank
    private String nickname;

    @NotBlank
    @Length(min = 4, message = "비밀번호는 최소 4자여야 합니다.")
    private String password;

    @NotBlank
    private String passwordCheck;
}