package com.sivalabs.blog.blog.domain;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogSubscriberService {
    private final BlogSubscriberRepository subscriberRepository;

    public BlogSubscriberService(BlogSubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Transactional
    public void subscribe(String email) {
        subscriberRepository.subscribe(email);
    }
}
