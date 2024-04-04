package com.api.service;

import com.api.component.Cache;
import com.api.component.CustomLogger;
import com.api.dao.EmailRepository;
import com.api.dao.EmailTypeRepository;
import com.api.entity.Email;
import com.api.entity.EmailType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.injection.MockInjection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailTypeServiceTest {
    @InjectMocks
    EmailTypeService emailTypeService;

    @Mock
    EmailTypeRepository emailTypeRepository;

    @Mock
    CustomLogger customLogger;

    @Mock
    Cache cache;

    @Mock
    EmailRepository emailRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateDomain_ByIdAndValidDomain_Success() {
        // Mock data
        Long id = 1L;
        String newDomain = "newexample.com";
        EmailType emailTypeEntity = new EmailType(1L, "example.com");
        emailTypeEntity.setEmails(new ArrayList<>()); // Initialize emails list

        // Mock repository behavior
        when(emailTypeRepository.findById(id)).thenReturn(Optional.of(emailTypeEntity));

        // Invoke method
        emailTypeService.updateDomain(id, newDomain);

        // Verify interactions and assertions
        verify(emailTypeRepository).findById(id);
        verify(emailTypeRepository).save(emailTypeEntity);
        verify(customLogger).logCachePut(newDomain);
    }

    private List<Email> getEmails(){
        List<Email> list = new ArrayList<>();
        list.add(new Email(1L,"vafda@gmail.com"));
        list.add(new Email(2L,"vafrteda@mail.com"));
        list.add(new Email(3L,"vafda@rambler.ru"));
        return list;
    }


    @Test
    public void testUpdateDomain_ByIdAndInvalidDomain_NoChanges() {
        // Mock data
        Long id = 1L;
        String newDomain = "invalid domain";
        EmailType emailTypeEntity = new EmailType("example.com");

        // Mock repository behavior
        when(emailTypeRepository.findById(id)).thenReturn(Optional.of(emailTypeEntity));

        // Invoke method
        emailTypeService.updateDomain(id, newDomain);

        // Verify interactions and assertions
        verify(emailTypeRepository).findById(id);
        verifyNoMoreInteractions(emailTypeRepository, customLogger);
    }

    @Test
    public void testUpdateDomain_ByIdAndNullDomain_NoChanges() {
        // Mock data
        Long id = 1L;
        String newDomain = null;
        EmailType emailTypeEntity = new EmailType("example.com");

        // Mock repository behavior
        when(emailTypeRepository.findById(id)).thenReturn(Optional.of(emailTypeEntity));

        // Invoke method
        emailTypeService.updateDomain(id, newDomain);

        // Verify interactions and assertions
        verify(emailTypeRepository).findById(id);
        verifyNoMoreInteractions(emailTypeRepository, customLogger);
    }

}