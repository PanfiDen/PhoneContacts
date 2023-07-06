package com.chiacademy.phonecontacts.user.controller;

import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;
import com.chiacademy.phonecontacts.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/user/{user-id}/contact")
    public Contact createContact(@PathVariable("user-id") Long userId,
                                 @RequestBody(required = false) ContactDTO newContact){
        return userService.createContact(userId, newContact);
    }

}
