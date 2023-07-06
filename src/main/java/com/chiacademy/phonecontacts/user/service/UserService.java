package com.chiacademy.phonecontacts.user.service;

import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;
import com.chiacademy.phonecontacts.contact.model.response.DeleteResponse;

public interface UserService {
    Contact createContact(Long userId, ContactDTO newContact);

    Contact editContactById(Long userId, Long contactId, ContactDTO contactToUpdate);

    DeleteResponse deleteContactById(Long userId, Long contactId);
}
