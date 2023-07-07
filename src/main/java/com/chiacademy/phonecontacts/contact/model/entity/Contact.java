package com.chiacademy.phonecontacts.contact.model.entity;


import com.chiacademy.phonecontacts.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

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
    @ElementCollection
    private Set<String> emails;
    @ElementCollection
    private Set<String> phones;
    @JsonIgnore
    @ManyToOne
    private User user;
}
