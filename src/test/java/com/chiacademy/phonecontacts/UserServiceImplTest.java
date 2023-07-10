package com.chiacademy.phonecontacts;

import com.chiacademy.phonecontacts.authentication.service.AuthenticationService;
import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;
import com.chiacademy.phonecontacts.contact.model.response.DeleteResponse;
import com.chiacademy.phonecontacts.contact.repository.ContactRepository;
import com.chiacademy.phonecontacts.exception.exception.ContactNotFoundException;
import com.chiacademy.phonecontacts.exception.exception.ForbiddenRequestException;
import com.chiacademy.phonecontacts.exception.exception.UserNotFoundException;
import com.chiacademy.phonecontacts.user.model.User;
import com.chiacademy.phonecontacts.user.repository.UserRepository;
import com.chiacademy.phonecontacts.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserServiceImpl userService;

    private User currentUser;
    private Contact contact;

    @BeforeEach
    public void create() {
        currentUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .password("secret")
                .build();
        contact = Contact.builder()
                .id(1L)
                .name("Test")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .user(currentUser)
                .build();
        currentUser.setContacts(new HashSet<>(List.of(contact)));
    }

    @Test
    void testCreateContact_ValidInput() {
        Long userId = currentUser.getId();
        Set<String> emails = new HashSet<>(Collections.singleton("new@example.com"));
        Set<String> phones = new HashSet<>(Collections.singleton("+0987654321"));
        ContactDTO newContact = ContactDTO.builder()
                .name("John Doe")
                .emails(emails)
                .phones(phones)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(authenticationService.isNotCurrentUser(any(User.class))).thenReturn(false);
        Contact result = userService.createContact(userId, newContact);

        assertNotNull(result);
        assertEquals(newContact.getName(), result.getName());
        assertEquals(newContact.getEmails(), result.getEmails());
        assertEquals(newContact.getPhones(), result.getPhones());
        assertEquals(currentUser, result.getUser());

        verify(contactRepository).save(result);
        verify(userRepository).save(currentUser);
    }

    @Test
    void testCreateContact_InvalidUserId() {
        Long userId = currentUser.getId();
        ContactDTO newContact = ContactDTO.builder()
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.createContact(userId, newContact));

        verify(contactRepository, never()).save(any(Contact.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testEditContactById_ValidInput() {
        Long userId = currentUser.getId();
        Long contactId = 1L;

        Set<String> emails = new HashSet<>(List.of("updated@example.com"));
        Set<String> phones = new HashSet<>(List.of("+918273645"));
        ContactDTO updatedContact = ContactDTO.builder()
                .name("Updated Name")
                .emails(emails)
                .phones(phones)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(authenticationService.isNotCurrentUser(currentUser)).thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenReturn(any(Contact.class));

        Contact result = userService.editContactById(userId, contactId, updatedContact);

        assertEquals(updatedContact.getName(), result.getName());
        assertEquals(updatedContact.getEmails(), new HashSet<>(result.getEmails()));
        assertEquals(updatedContact.getPhones(), new HashSet<>(result.getPhones()));
        assertEquals(currentUser, result.getUser());

        verify(contactRepository).save(result);
        verify(userRepository).save(currentUser);
    }

    @Test
    void testEditContactById_InvalidUserId() {
        Long userId = currentUser.getId();
        Long contactId = 1L;
        ContactDTO updatedContact = ContactDTO.builder()
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.editContactById(userId, contactId, updatedContact));

        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void testEditContactById_InvalidContactId() {
        Long userId = currentUser.getId();
        Long contactId = 1L;
        ContactDTO updatedContact = ContactDTO.builder()
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> userService.editContactById(userId, contactId, updatedContact));

        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void testEditContactById_UserNotAuthorized() {
        Long userId = currentUser.getId();
        Long contactId = 1L;
        ContactDTO updatedContact = ContactDTO.builder()
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .build();

        Contact existingContact = Contact.builder()
                .id(contactId)
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .user(currentUser)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(authenticationService.isNotCurrentUser(currentUser)).thenReturn(true);

        assertThrows(ForbiddenRequestException.class, () -> userService.editContactById(userId, contactId, updatedContact));

        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void testDeleteContactById_ValidInput() {
        Long userId = currentUser.getId();
        Long contactId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(authenticationService.isNotCurrentUser(currentUser)).thenReturn(false);

        DeleteResponse result = userService.deleteContactById(userId, contactId);

        assertNotNull(result);
        assertEquals(contactId, result.getId());
        assertEquals("Contact successfully deleted", result.getStatus());

        verify(contactRepository).deleteById(contactId);
    }

    @Test
    void testDeleteContactById_InvalidUserId() {
        Long userId = currentUser.getId();
        Long contactId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteContactById(userId, contactId));

        verify(contactRepository, never()).deleteById(contactId);
    }

    @Test
    void testDeleteContactById_UserNotAuthorized() {
        Long userId = currentUser.getId();
        Long contactId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(authenticationService.isNotCurrentUser(currentUser)).thenReturn(true);

        assertThrows(ForbiddenRequestException.class, () -> userService.deleteContactById(userId, contactId));

        verify(contactRepository, never()).deleteById(contactId);
    }

    @Test
    void testGetAllContactByUserId_ValidInput() {
        Long userId = currentUser.getId();

        Contact contact2 = Contact.builder()
                .id(2L)
                .name("Jane Smith")
                .emails(new HashSet<>(List.of("jane@example.com")))
                .phones(new HashSet<>(List.of("+9876543210")))
                .user(currentUser)
                .build();

        currentUser.setContacts(new HashSet<>(Arrays.asList(contact, contact2)));

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

        Set<Contact> result = userService.getAllContactByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(contact));
        assertTrue(result.contains(contact2));
    }

    @Test
    void testGetAllContactByUserId_InvalidUserId() {
        Long userId = currentUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getAllContactByUserId(userId));
    }

    @Test
    void testIsNotCurrentUser(){
        when(authenticationService.isNotCurrentUser(currentUser)).thenThrow(ForbiddenRequestException.class);

        assertThrows(ForbiddenRequestException.class, () ->{
            authenticationService.isNotCurrentUser(currentUser);
        });
    }
}
