package com.example.enlaco.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsersDTO {

    private Integer userid;
    private String  email;
    private String  password;
    private String  nickname;
    private String  oauthtype;
    private String  tel;
    private String  address;
    private String role;
    private LocalDateTime regDate;
}
