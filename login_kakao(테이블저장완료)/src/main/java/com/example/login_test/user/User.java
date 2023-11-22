package com.example.login_test.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="user_tb")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length = 100, nullable = false, unique = true)
    private String email;
    @Column(length = 256, nullable = false)
    private String password;
    @Column(length = 45, nullable = false)
    private String username;
    @Column(length = 50, nullable = false)
    private String phoneNumber;
    @Column(length = 30)
    @Convert(converter = StringArrayConverter.class)
    private List<String> roles = new ArrayList<>();
    @Builder
    public User(int id, String email, String password, String username, String phoneNumber, List<String> roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    public void output(){
        System.out.println(id);
        System.out.println(email);
        System.out.println(password);
        System.out.println(username);
        System.out.println(roles);
    }
}
