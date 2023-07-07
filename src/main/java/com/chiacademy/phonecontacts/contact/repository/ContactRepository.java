package com.chiacademy.phonecontacts.contact.repository;

import com.chiacademy.phonecontacts.contact.model.entity.Contact;
import com.chiacademy.phonecontacts.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    boolean existsByNameEqualsIgnoreCaseAndUser(String name, User user);

    Set<Contact> findByUser(User user);
}
