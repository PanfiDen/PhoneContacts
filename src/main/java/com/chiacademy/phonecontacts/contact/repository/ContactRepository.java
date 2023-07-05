package com.chiacademy.phonecontacts.contact.repository;

import com.chiacademy.phonecontacts.contact.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}
