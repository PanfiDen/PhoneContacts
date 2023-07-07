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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
                .emails(newContact.getEmails().stream()
                        .filter(this::isValidEmail)
                        .collect(Collectors.toSet()))
                .phones(newContact.getPhones().stream()
                        .filter(this::isValidPhone)
                        .collect(Collectors.toSet()))
                .user(creator)
                .build();

        hasDuplicate(newContact, creator);

        contactRepository.save(contact);
        userRepository.save(creator);
        return contact;
    }

    @Override
    public Contact editContactById(Long userId, Long contactId, ContactDTO contactToUpdate) {
        User editor = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Contact contact = contactRepository.findById(contactId).orElseThrow(ContactNotFoundException::new);
        if (isNotCurrentUser(editor)){
            throw new ForbiddenRequestException();
        }

        hasDuplicate(contactToUpdate, editor);
        checkForChanges(contactToUpdate, contact);

        return contactRepository.save(contact);
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

    @Override
    public Set<Contact> getAllContactByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return user.getContacts();
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
            contact.setEmails(contactToUpdate.getEmails().stream()
                    .filter(this::isValidEmail)
                    .collect(Collectors.toSet()));
        }
        if (contactToUpdate.getPhones() != null && !contactToUpdate.getPhones().equals(contact.getPhones())) {
            contact.setPhones(contactToUpdate.getPhones().stream()
                    .filter(this::isValidPhone)
                    .collect(Collectors.toSet()));
        }
    }

    private boolean isValidEmail(String email) {
        if (!email.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b")) {
            throw new BadRequestException("Invalid email format");
        }
        return true;
    }

    private boolean isValidPhone(String phoneNumber) {
        if (!phoneNumber.matches("^\\+?[0-9]{1,3}-?[0-9]{6,14}$")) {
            throw new BadRequestException("Invalid phone number format");
        }
        return true;
    }

    private void hasDuplicate(ContactDTO contactDTO, User currentUser) {
        if (contactDTO.getName() == null || contactDTO.getEmails() == null || contactDTO.getPhones() == null)
            throw new BadRequestException("The fields mustn't be empty.");

        if (contactRepository.existsByNameEqualsIgnoreCaseAndUser(contactDTO.getName(), currentUser)){
            throw new BadRequestException("Name must be unique");
        }

        Set<String> uniqueEmails = new HashSet<>();
        Set<String> uniquePhones = new HashSet<>();

        for (Contact userContact : currentUser.getContacts()) {
            if (userContact.getName().equalsIgnoreCase(contactDTO.getName())) {
                throw new BadRequestException("Name must be unique");
            }

            uniqueEmails.addAll(userContact.getEmails());
            uniquePhones.addAll(userContact.getPhones());
        }

        if (hasCommonElement(uniqueEmails, contactDTO.getEmails())) {
            throw new BadRequestException("Email must be unique");
        }

        if (hasCommonElement(uniquePhones, contactDTO.getPhones())) {
            throw new BadRequestException("Phone must be unique");
        }
    }

    private boolean hasCommonElement(Set<String> set1, Set<String> list2) {
        for (String element : list2) {
            if (set1.contains(element)) {
                return true;
            }
        }
        return false;
    }
}
