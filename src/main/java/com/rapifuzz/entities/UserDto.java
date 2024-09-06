package com.rapifuzz.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private String name;

    private String email;

    private String password;

    private String address;

    private String pinCode;

    private String city;

    private String country;

    private String phoneNumber;

}