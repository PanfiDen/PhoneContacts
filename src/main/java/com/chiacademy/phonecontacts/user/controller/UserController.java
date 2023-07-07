package com.chiacademy.phonecontacts.user.controller;

import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;
import com.chiacademy.phonecontacts.contact.model.response.DeleteResponse;
import com.chiacademy.phonecontacts.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping("/user/{user-id}/contact")
    public Contact createContact(@PathVariable("user-id") Long userId,
                                 @RequestBody(required = false) ContactDTO newContact){
        return userService.createContact(userId, newContact);
    }

    @PatchMapping("/user/{user-id}/contact/{contact-id}")
    public Contact editContactById(@PathVariable("user-id") Long userId,
                                   @PathVariable("contact-id") Long contactId,
                                   @RequestBody(required = false) ContactDTO contactToUpdate) {
        return userService.editContactById(userId, contactId, contactToUpdate);
    }

    @DeleteMapping("/user/{user-id}/contact/{contact-id}")
    public DeleteResponse deleteContactById(@PathVariable("user-id") Long userId,
                                          @PathVariable("contact-id") Long contactId) {
        return userService.deleteContactById(userId, contactId);
    }

    @GetMapping("/user/{user-id}/contact")
    public Set<Contact> getAllContactByUserId(@PathVariable("user-id") Long userId){
        return userService.getAllContactByUserId(userId);
    }
}
