package org.example.restservlet.service.impl;

import org.example.restservlet.entity.Publisher;
import org.example.restservlet.exception.EntityNotFoundException;
import org.example.restservlet.repository.PublisherRepository;
import org.example.restservlet.service.PublisherService;

import java.text.MessageFormat;
import java.util.List;

public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherServiceImpl(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public List<Publisher> findAll() {
        return publisherRepository.findAll();
    }

    @Override
    public Publisher findById(Long id) {

        return publisherRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException((MessageFormat.format(
                "Publisher с id {0} не найден", id))));
    }

    @Override
    public Publisher insert(Publisher publisher) {
        return publisherRepository.insert(publisher);
    }

    @Override
    public Publisher update(Long id, Publisher publisher) {
        Publisher existed = findById(id);

        existed.setName(publisher.getName());
        return publisherRepository.update(existed);
    }

    @Override
    public void deleteById(Long id) {
        publisherRepository.deleteById(id);
    }
}
