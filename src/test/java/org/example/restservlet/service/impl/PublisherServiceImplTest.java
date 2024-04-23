package org.example.restservlet.service.impl;

import org.example.restservlet.entity.Publisher;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.repository.PublisherRepository;
import org.example.restservlet.service.PublisherService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class PublisherServiceImplTest {

    private PublisherRepository publisherRepository = Mockito.mock(PublisherRepository.class);

    private PublisherService publisherService = new PublisherServiceImpl(publisherRepository);

    @Test
    public void whenGetAll_thenCorrectCount() {
        List<Publisher> expected = List.of(
                new Publisher(1L, "P1"),
                new Publisher(2L, "P2"),
                new Publisher(3L, "P3"));
        Mockito.when(publisherRepository.findAll()).thenReturn(expected);

        List<Publisher> actual = publisherService.findAll();

        Assert.assertEquals(expected, actual);
        Mockito.verify(publisherRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void whenGetById_thenUsedRepository() {
        Publisher expected = new Publisher(5L, "test");
        Mockito.when(publisherRepository.findById(5L)).thenReturn(Optional.of(expected));

        Publisher actual = publisherService.findById(5L);

        Assert.assertEquals(expected, actual);
        Mockito.verify(publisherRepository, Mockito.times(1)).findById(5L);
    }

    @Test
    public void whenGetNotExistedId_thenThrow(){
        Mockito.when(publisherRepository.findById(5L)).thenReturn(Optional.empty());

        Assert.assertThrows(EntityNotFoundException.class, () -> publisherService.findById(5L));
    }

    @Test
    public void whenInsert_thenUsedRepository() {
        Publisher inserted = new Publisher();
        inserted.setName("test");

        Publisher expected = new Publisher(1L, "test");
        Mockito.when(publisherRepository.insert(inserted)).thenReturn(expected);

        Publisher actual = publisherService.insert(inserted);

        Assert.assertEquals(expected, actual);
        Mockito.verify(publisherRepository, Mockito.times(1)).insert(inserted);
    }

    @Test
    public void whenUpdate_thenUseRepository() {
        Publisher updated = new Publisher(2L, "testtest");
        Publisher expected = new Publisher(2L, "testtest");

        Mockito.when(publisherRepository.findById(2L)).thenReturn(Optional.of(updated));
        Mockito.when(publisherRepository.update(updated)).thenReturn(expected);

        Publisher actual = publisherService.update(2L, updated);

        Assert.assertEquals(expected, actual);
        Mockito.verify(publisherRepository, Mockito.times(1)).update(updated);
        Mockito.verify(publisherRepository, Mockito.times(1)).findById(2L);
    }

    @Test
    public void whenDelete_thenUserRepository() {

        publisherService.deleteById(2L);
        Mockito.verify(publisherRepository, Mockito.times(1)).deleteById(2L);
    }

}
