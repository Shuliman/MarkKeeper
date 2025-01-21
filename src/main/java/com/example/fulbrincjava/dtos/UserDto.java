package com.example.fulbrincjava.dtos;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserDto {
    private String email;

    private String password;

    private String login;

}
