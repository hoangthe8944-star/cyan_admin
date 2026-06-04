package com.example.cyan.contact.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.example.cyan.contact.model.Contact;

@Repository
public interface ContactRepository extends MongoRepository<Contact, String> {
}
