package com.example.enlaco.Entity;

import com.example.enlaco.Constant.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
@SequenceGenerator(
        name = "user_SEQ",
        sequenceName = "user_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_SEQ")
    private Integer id;
    @Column(name="email", columnDefinition="VARCHAR(100)", nullable=false, unique=true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name="oauth_type", columnDefinition="VARCHAR(50)")
    private String oauthType;

    @Column(name="tel", columnDefinition = "VARCHAR(30)")
    private String tel;

    @Column(name="address", columnDefinition = "VARCHAR(200)")
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;
}
