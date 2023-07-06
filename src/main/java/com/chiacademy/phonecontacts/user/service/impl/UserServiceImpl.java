package com.chiacademy.phonecontacts.user.service.impl;

import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;
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
    @Override
    public Contact createContact(Long userId, ContactDTO newContact) {
        User creator = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if(isNotCurrentUser(creator)){
            throw new ForbiddenRequestException();
        }
        return Contact.builder()
                .name(newContact.getName())
                .emails(newContact.getEmails())
                .phones(newContact.getPhones())
                .user(creator)
                .build();
    }


    public boolean isNotCurrentUser(User user) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return !email.equalsIgnoreCase(user.getEmail());
    }
}
