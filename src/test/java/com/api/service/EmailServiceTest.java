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
        Mockito.when(emailRepository.findAll()).thenReturn(getEmails());
        List<EmailDTO> res = emailService.getEmails("all");
        Assertions.assertEquals(3,res.size());
        Assertions.assertNotNull(res.get(0));
    }
    @Test
    void shouldReturnProcessedText() {
        String text1 = "rooror@mail.com give+375292556867";
        String text2 = "rooror@mail.com give";
        String text3 = "give+375292556867";
        String text4 = "give";
        Assertions.assertEquals(" give",emailService.getConfidentialText(text1));
        Assertions.assertEquals(" give",emailService.getConfidentialText(text2));
        Assertions.assertEquals("give",emailService.getConfidentialText(text3));
        Assertions.assertEquals("give",emailService.getConfidentialText(text4));
    }
    @Test
    void shouldReturnEmailsByEmailType(){
        List<Email> list = new ArrayList<>();
        list.add(new Email(1L,"gagagga@mail.ru"));
        list.add(new Email(2L,"wqeqe@mail.ru"));
        Mockito.when(emailRepository.findByEmailTypeDomain("mail.ru")).thenReturn(list);
        List<EmailDTO> emailDTOList = new ArrayList<>();
        emailDTOList = emailService.getEmailsByEmailType("mail.ru");
        Assertions.assertNotNull(emailDTOList);
        Assertions.assertEquals(emailDTOList.size(),2);
    }
    @Test
    void shouldReturnUpdateEmailsByName(){
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        Email emailEntity = new Email(oldEmail);
        EmailType emailType = new EmailType("example.com");

        // Mock repository behavior
        Mockito.when(emailRepository.findByName(oldEmail)).thenReturn(emailEntity);
        Mockito.when(emailTypeRepository.findByDomain("example.com")).thenReturn(emailType);

        // Invoke method
        emailService.updateEmail(oldEmail, newEmail);

        Mockito.verify(emailRepository,Mockito.times(1)).findByName(Mockito.any());
    }
    @Test
    void shouldReturnUpdateEmailsById(){
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        EmailType emailType = new EmailType("example.com");

        // Mock repository behavior
        Mockito.when(emailRepository.findById(1L)).thenReturn(Optional.of(new Email(1L,oldEmail)));
        Mockito.when(emailTypeRepository.findByDomain("example.com")).thenReturn(emailType);

        // Invoke method
        emailService.updateEmail(1L, newEmail);

        Mockito.verify(emailRepository,Mockito.times(1)).findById(1L);
    }
    @Test
    void shouldReturnUpdateEmailsByIdWithoutDomain(){
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        EmailType emailType = new EmailType("example.com");

        // Mock repository behavior
        Mockito.when(emailRepository.findById(1L)).thenReturn(Optional.of(new Email(1L,oldEmail)));
        Mockito.when(emailTypeRepository.findByDomain("example.com")).thenReturn(null);

        // Invoke method
        emailService.updateEmail(1L, newEmail);

        Mockito.verify(emailRepository,Mockito.times(1)).findById(1L);
    }
    @Test
    void shouldReturnUpdateEmailsByNameWithoutDomain(){
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        Email emailEntity = new Email(oldEmail);
        EmailType emailType = new EmailType("example.com");

        // Mock repository behavior
        Mockito.when(emailRepository.findByName(oldEmail)).thenReturn(emailEntity);
        Mockito.when(emailTypeRepository.findByDomain("example.com")).thenReturn(null);

        // Invoke method
        emailService.updateEmail(oldEmail, newEmail);

        Mockito.verify(emailRepository,Mockito.times(1)).findByName(Mockito.any());
    }


    @Transactional
    public List<EmailDTO> getEmailsByEmailType(String text) {
        List<EmailDTO> strings = new ArrayList<>();
        List<Email> emailEntities;
        emailEntities = emailRepository.findByEmailTypeDomain(text);
        for (int i = 0; i < emailEntities.size(); i++) {
            cache.put(emailEntities.get(i).getEmail(), emailEntities.get(i));
            customLogger.logCachePut(emailEntities.get(i).getEmail());
            strings.add(new EmailDTO(emailEntities.get(i).getId().toString() + ". " + emailEntities.get(i).getEmail()));
        }

        return strings;
    }
    private List<Email> getEmails(){
        List<Email> list = new ArrayList<>();
        list.add(new Email(1L,"vafda@gmail.com"));
        list.add(new Email(2L,"vafrteda@mail.com"));
        list.add(new Email(3L,"vafda@rambler.ru"));
        return list;
    }
}


