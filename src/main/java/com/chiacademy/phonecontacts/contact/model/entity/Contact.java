package com.chiacademy.phonecontacts.contact.model.entity;


import com.chiacademy.phonecontacts.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany
    private List<Email> emails;
    @OneToMany
    private List<Phone> phones;

    @ManyToOne
    private User user;
}
