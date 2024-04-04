package com.api.service;

import com.api.component.Cache;
import com.api.component.CustomLogger;
import com.api.dao.EmailRepository;
import com.api.dao.EmailTypeRepository;
import com.api.dto.EmailDTO;
import com.api.entity.Email;
import com.api.entity.EmailType;
import com.api.exceptions.ServiceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class EmailServiceTest {

    @InjectMocks
    EmailService emailService;

    @Mock
    EmailTypeRepository emailTypeRepository;

    @Mock
    Cache cache;

    @Mock
    EmailRepository emailRepository;

    @Mock
    CustomLogger customLogger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldReturnAllEmails() {
        when(emailRepository.findAll()).thenReturn(getEmails());
        List<EmailDTO> res = emailService.getEmails("all");
        Assertions.assertEquals(3, res.size());
        Assertions.assertNotNull(res.get(0));
    }

    @Test
    void shouldReturnProcessedText() {
        String text1 = "rooror@mail.com give+375292556867";
        String text2 = "rooror@mail.com give";
        String text3 = "give+375292556867";
        String text4 = "give";
        Assertions.assertEquals(" give", emailService.getConfidentialText(text1));
        Assertions.assertEquals(" give", emailService.getConfidentialText(text2));
        Assertions.assertEquals("give", emailService.getConfidentialText(text3));
        Assertions.assertEquals("give", emailService.getConfidentialText(text4));
    }

    @Test
    void shouldReturnEmailsByEmailType() {
        List<Email> list = new ArrayList<>();
        list.add(new Email(1L, "gagagga@mail.ru"));
        list.add(new Email(2L, "wqeqe@mail.ru"));
        when(emailRepository.findByEmailTypeDomain("mail.ru")).thenReturn(list);
        List<EmailDTO> emailDTOList = new ArrayList<>();
        emailDTOList = emailService.getEmailsByEmailType("mail.ru");
        Assertions.assertNotNull(emailDTOList);
        Assertions.assertEquals(emailDTOList.size(), 2);
    }

    @Test
    void shouldReturnUpdateEmailsByName() {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        Email emailEntity = new Email(oldEmail);
        EmailType emailType = new EmailType("example.com");

        // Mock repository behavior
        when(emailRepository.findByName(oldEmail)).thenReturn(emailEntity);
        when(emailTypeRepository.findByDomain("example.com")).thenReturn(emailType);

        // Invoke method
        emailService.updateEmail(oldEmail, newEmail);

        verify(emailRepository, Mockito.times(1)).findByName(Mockito.any());
    }

    @Test
    void shouldReturnUpdateEmailsById() {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        EmailType emailType = new EmailType("example.com");

        // Mock repository behavior
        when(emailRepository.findById(1L)).thenReturn(Optional.of(new Email(1L, oldEmail)));
        when(emailTypeRepository.findByDomain("example.com")).thenReturn(emailType);

        // Invoke method
        emailService.updateEmail(1L, newEmail);

        verify(emailRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void shouldReturnUpdateEmailsByIdWithoutDomain() {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        EmailType emailType = new EmailType("example.com");

        // Mock repository behavior
        when(emailRepository.findById(1L)).thenReturn(Optional.of(new Email(1L, oldEmail)));
        when(emailTypeRepository.findByDomain("example.com")).thenReturn(null);

        // Invoke method
        emailService.updateEmail(1L, newEmail);

        verify(emailRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void shouldReturnUpdateEmailsByNameWithoutDomain() {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        Email emailEntity = new Email(oldEmail);
        EmailType emailType = new EmailType("example.com");

        // Mock repository behavior
        when(emailRepository.findByName(oldEmail)).thenReturn(emailEntity);
        when(emailTypeRepository.findByDomain("example.com")).thenReturn(null);

        // Invoke method
        emailService.updateEmail(oldEmail, newEmail);

        verify(emailRepository, Mockito.times(1)).findByName(Mockito.any());
    }

    @Test
    void testDeleteEmailEmailExistsSuccess() {
        // Mock data
        Long id = 1L;
        String email = "test@example.com";
        Email emailEntity = new Email(email);

        // Mock repository behavior
        when(emailRepository.findById(id)).thenReturn(Optional.of(emailEntity));

        // Invoke method
        emailService.deleteEmail(id);

        // Verify interactions and assertions
        verify(emailRepository).findById(id);
        verify(cache).remove(email);
        verify(customLogger).logCacheRemove(email);
        verify(emailRepository).delete(emailEntity);
    }

    @Test
    public void testDeleteEmailEmailNotFoundErrorLogged() {
        // Mock data
        Long id = 1L;

        // Mock repository behavior
        when(emailRepository.findById(id)).thenReturn(Optional.empty());

        // Invoke method and verify exception
        assertThrows(ServiceException.class, () -> emailService.deleteEmail(id));

        // Verify interactions and assertions
        verify(emailRepository).findById(id);
        verify(customLogger).logError("Email is not found");
        verifyNoMoreInteractions(cache);
        verifyNoMoreInteractions(emailRepository);
    }
    private List<Email> getEmails(){
        List<Email> list = new ArrayList<>();
        list.add(new Email(1L,"vafda@gmail.com"));
        list.add(new Email(2L,"vafrteda@mail.com"));
        list.add(new Email(3L,"vafda@rambler.ru"));
        return list;
    }

}


