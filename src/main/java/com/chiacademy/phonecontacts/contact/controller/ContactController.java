package com.chiacademy.phonecontacts.contact.controller;

import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;
import com.chiacademy.phonecontacts.contact.model.response.DeleteResponse;
import com.chiacademy.phonecontacts.contact.service.ContactService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@AllArgsConstructor
public class ContactController {
    private ContactService contactService;

    @PostMapping("/user/{user-id}/contact")
    public Contact createContact(@PathVariable("user-id") Long userId,
                                 @RequestBody(required = false) ContactDTO newContact){
        return contactService.createContact(userId, newContact);
    }

    @PatchMapping("/user/{user-id}/contact/{contact-id}")
    public Contact editContactById(@PathVariable("user-id") Long userId,
                                   @PathVariable("contact-id") Long contactId,
                                   @RequestBody(required = false) ContactDTO contactToUpdate) {
        return contactService.editContactById(userId, contactId, contactToUpdate);
    }

    @DeleteMapping("/user/{user-id}/contact/{contact-id}")
    public DeleteResponse deleteContactById(@PathVariable("user-id") Long userId,
                                          @PathVariable("contact-id") Long contactId) {
        return contactService.deleteContactById(userId, contactId);
    }

    @GetMapping("/user/{user-id}/contact")
    public Set<Contact> getAllContactByUserId(@PathVariable("user-id") Long userId){
        return contactService.getAllContactByUserId(userId);
    }
}
