package org.example.restservlet.repository.impl;

import org.example.restservlet.AbstractTest;
import org.example.restservlet.entity.Publisher;
import org.junit.Assert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.ext.ScriptUtils;

import java.util.Optional;

class PublisherRepositoryImplTest extends AbstractTest {

    private final PublisherRepositoryImpl publisherRepository = new PublisherRepositoryImpl(dataSource);

    @BeforeEach
    public void dropCreateSchema() {
        ScriptUtils.runInitScript(jdbcDelegate, "db/drop_create_schema.sql");
        ScriptUtils.runInitScript(jdbcDelegate, "db/fill_database.sql");
    }

    @Test
    void whenGetAll_thenCorrectCount() {
        int count = publisherRepository.findAll().size();

        Assert.assertEquals(4, count);
    }

    @Test
    void whenGetById_thenCorrectElement() {
        Publisher publisher = publisherRepository.findById(2L).get();

        Assert.assertEquals("NINTENDO", publisher.getName());
    }

    @Test
    void whenGetIncorrectId_thenOptionalEmpty() {
        Optional<Publisher> publisher = publisherRepository.findById(10L);
        Assert.assertTrue(publisher.isEmpty());
    }

    @Test
    void whenInsert_thenCorrectAdded() {
        Publisher publisher = new Publisher();
        publisher.setName("TEST");

        Publisher newPublisher = publisherRepository.insert(publisher);
        Assert.assertEquals(Long.valueOf(5),  newPublisher.getId());

        Publisher existed = publisherRepository.findById(5L).get();
        Assert.assertEquals("TEST", existed.getName());

    }

    @Test
    void whenUpdate_thenCorrectData() {
        Publisher publisher = publisherRepository.findById(2L).get();
        publisher.setName("TEST");
        publisherRepository.update(publisher);

        Publisher existed = publisherRepository.findById(2L).get();
        Assert.assertEquals("TEST", existed.getName());
    }

    @Test
    void whenDelete_thenDeletedAndCorrectCount() {
        publisherRepository.deleteById(4L);

        Optional<Publisher> publisher = publisherRepository.findById(4L);
        Assert.assertEquals(Optional.empty(), publisher);

        int count = publisherRepository.findAll().size();
        Assert.assertEquals(3, count);
    }


}
