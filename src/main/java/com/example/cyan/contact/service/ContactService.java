package com.example.cyan.contact.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.cyan.common.exception.ResourceNotFoundException;
import com.example.cyan.contact.model.Contact;
import com.example.cyan.contact.repository.ContactRepository;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Contact create(Contact contact) {
        return contactRepository.save(contact);
    }

    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    public Contact findById(String id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact message not found: " + id));
    }

    public void delete(String id) {
        if (!contactRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contact message not found: " + id);
        }
        contactRepository.deleteById(id);
    }
}
