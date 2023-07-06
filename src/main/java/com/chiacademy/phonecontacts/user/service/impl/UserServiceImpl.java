package com.chiacademy.phonecontacts.user.service.impl;

import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;
import com.chiacademy.phonecontacts.contact.model.response.DeleteResponse;
import com.chiacademy.phonecontacts.contact.repository.ContactRepository;
import com.chiacademy.phonecontacts.exception.exception.BadRequestException;
import com.chiacademy.phonecontacts.exception.exception.ContactNotFoundException;
import com.chiacademy.phonecontacts.exception.exception.ForbiddenRequestException;
import com.chiacademy.phonecontacts.exception.exception.UserNotFoundException;
import com.chiacademy.phonecontacts.user.model.User;
import com.chiacademy.phonecontacts.user.repository.UserRepository;
import com.chiacademy.phonecontacts.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private ContactRepository contactRepository;
    @Override
    public Contact createContact(Long userId, ContactDTO newContact) {
        User creator = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if(isNotCurrentUser(creator)){
            throw new ForbiddenRequestException();
        }

        Contact contact = Contact.builder()
                .name(newContact.getName())
                .emails(newContact.getEmails())
                .phones(newContact.getPhones())
                .user(creator)
                .build();
        contactRepository.save(contact);
        return contact;
    }

    @Override
    public Contact editContactById(Long userId, Long contactId, ContactDTO contactToUpdate) {

        User editor = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Contact contact = contactRepository.findById(contactId).orElseThrow(ContactNotFoundException::new);
        if (isNotCurrentUser(editor)){
            throw new ForbiddenRequestException();
        }
        checkForChanges(contactToUpdate, contact);
        contactRepository.save(contact);

        return contact;
    }

    @Override
    public DeleteResponse deleteContactById(Long userId, Long contactId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (isNotCurrentUser(user)){
            throw new ForbiddenRequestException();
        }
        contactRepository.deleteById(contactId);
        return DeleteResponse.builder().id(contactId).status("Contact successfully deleted").build();
    }


    public boolean isNotCurrentUser(User user) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return !email.equalsIgnoreCase(user.getEmail());
    }

    private void checkForChanges(ContactDTO contactToUpdate, Contact contact) {
        if (contactToUpdate == null) {
            throw new BadRequestException("No changes were applied");
        }
        if (contactToUpdate.getName() != null && !contactToUpdate.getName().equals(contact.getName())) {
            contact.setName(contactToUpdate.getName());
        }
        if (contactToUpdate.getEmails() != null && !contactToUpdate.getEmails().equals(contact.getEmails())) {
            contact.setEmails(contactToUpdate.getEmails());
        }
        if (contactToUpdate.getPhones() != null && !contactToUpdate.getPhones().equals(contact.getPhones())) {
            contact.setPhones(contactToUpdate.getPhones());
        }
    }
}
