package com.chiacademy.phonecontacts.user.model;

import com.chiacademy.phonecontacts.contact.model.Contact;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email
    private String email;
    private String password;
    @OneToMany
    private List<Contact> contacts;
}
