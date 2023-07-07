package com.chiacademy.phonecontacts;

import com.chiacademy.phonecontacts.contact.model.dto.ContactDTO;
import com.chiacademy.phonecontacts.contact.model.entity.Contact;
import com.chiacademy.phonecontacts.contact.model.response.DeleteResponse;
import com.chiacademy.phonecontacts.contact.repository.ContactRepository;
import com.chiacademy.phonecontacts.exception.exception.ForbiddenRequestException;
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

    @InjectMocks
    private UserServiceImpl userService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .build();
    }

    @Test
    void testCreateContact_ValidInput() {
        Long userId = currentUser.getId();
        User creator = User.builder()
                .id(userId)
                .email("user@example.com").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(creator));
        when(userService.isNotCurrentUser(any(User.class))).thenReturn(false);
        ContactDTO newContact = ContactDTO.builder()
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .build();




        Contact result = userService.createContact(userId, newContact);

        assertNotNull(result);
        assertEquals(newContact.getName(), result.getName());
        assertEquals(newContact.getEmails(), new HashSet<>(result.getEmails()));
        assertEquals(newContact.getPhones(), new HashSet<>(result.getPhones()));
        assertEquals(creator, result.getUser());

        verify(contactRepository).save(result);
        verify(userRepository).save(creator);
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
        when(userService.isNotCurrentUser(any(User.class))).thenThrow(ForbiddenRequestException.class);

        assertThrows(ForbiddenRequestException.class, () -> userService.createContact(userId, newContact));

        verify(contactRepository, never()).save(any(Contact.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testEditContactById_ValidInput() {
        Long userId = currentUser.getId();
        Long contactId = 1L;

        User editor = User.builder()
                .id(userId)
                .email("editor@example.com")
                .build();

        Contact existingContact = Contact.builder()
                .id(contactId)
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .user(editor)
                .build();

        ContactDTO updatedContact = ContactDTO.builder()
                .name("Updated Name")
                .emails(new HashSet<>(List.of("updated@example.com")))
                .phones(new HashSet<>(List.of("+9876543210")))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(editor));
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(userService.isNotCurrentUser(editor)).thenReturn(false);

        Contact result = userService.editContactById(userId, contactId, updatedContact);

        assertNotNull(result);
        assertEquals(updatedContact.getName(), result.getName());
        assertEquals(updatedContact.getEmails(), new HashSet<>(result.getEmails()));
        assertEquals(updatedContact.getPhones(), new HashSet<>(result.getPhones()));
        assertEquals(editor, result.getUser());

        verify(contactRepository).save(result);
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
        when(userService.isNotCurrentUser(any(User.class))).thenThrow(ForbiddenRequestException.class);

        assertThrows(ForbiddenRequestException.class, () -> userService.editContactById(userId, contactId, updatedContact));

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

        User editor = User.builder()
                .id(userId)
                .email("editor@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(editor));
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());
        when(userService.isNotCurrentUser(editor)).thenReturn(false);

        assertThrows(ForbiddenRequestException.class, () -> userService.editContactById(userId, contactId, updatedContact));

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

        User editor = User.builder()
                .id(2L)
                .email("anotheruser@example.com")
                .build();

        Contact existingContact = Contact.builder()
                .id(contactId)
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .user(editor)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(editor));
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(userService.isNotCurrentUser(editor)).thenReturn(true);

        assertThrows(ForbiddenRequestException.class, () -> userService.editContactById(userId, contactId, updatedContact));

        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void testDeleteContactById_ValidInput() {
        Long userId = currentUser.getId();
        Long contactId = 1L;

        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .build();

        Contact existingContact = Contact.builder()
                .id(contactId)
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .user(user)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.isNotCurrentUser(user)).thenReturn(false);
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));

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
        when(userService.isNotCurrentUser(any(User.class))).thenThrow(ForbiddenRequestException.class);

        assertThrows(ForbiddenRequestException.class, () -> userService.deleteContactById(userId, contactId));

        verify(contactRepository, never()).deleteById(contactId);
    }

    @Test
    void testDeleteContactById_UserNotAuthorized() {
        Long userId = currentUser.getId();
        Long contactId = 1L;

        User user = User.builder()
                .id(2L)
                .email("anotheruser@example.com")
                .build();

        Contact existingContact = Contact.builder()
                .id(contactId)
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .user(user)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.isNotCurrentUser(any(User.class))).thenThrow(ForbiddenRequestException.class);
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));

        assertThrows(ForbiddenRequestException.class, () -> userService.deleteContactById(userId, contactId));

        verify(contactRepository, never()).deleteById(contactId);
    }

    @Test
    void testGetAllContactByUserId_ValidInput() {
        Long userId = currentUser.getId();

        Contact contact1 = Contact.builder()
                .id(1L)
                .name("John Doe")
                .emails(new HashSet<>(List.of("john@example.com")))
                .phones(new HashSet<>(List.of("+1234567890")))
                .user(currentUser)
                .build();

        Contact contact2 = Contact.builder()
                .id(2L)
                .name("Jane Smith")
                .emails(new HashSet<>(List.of("jane@example.com")))
                .phones(new HashSet<>(List.of("+9876543210")))
                .user(currentUser)
                .build();

        currentUser.setContacts(new HashSet<>(Arrays.asList(contact1, contact2)));

        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));

        Set<Contact> result = userService.getAllContactByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(contact1));
        assertTrue(result.contains(contact2));
    }

    @Test
    void testGetAllContactByUserId_InvalidUserId() {
        Long userId = currentUser.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(userService.isNotCurrentUser(any(User.class))).thenThrow(ForbiddenRequestException.class);

        assertThrows(ForbiddenRequestException.class, () -> userService.getAllContactByUserId(userId));
    }
}
