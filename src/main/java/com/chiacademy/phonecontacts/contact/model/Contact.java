package com.chiacademy.phonecontacts.contact.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany
    private List<ContactEmail> emails;
    @OneToMany
    private List<ContactPhone> phone;
}
