package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.blog.domain.models.CreateMessageParams;
import com.sivalabs.blog.shared.entities.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlogMessageService {
    private final BlogMessageRepository messageRepository;

    public BlogMessageService(BlogMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public void createMessage(CreateMessageParams params) {
        var message = new Message();
        message.setName(params.name());
        message.setEmail(params.email());
        message.setSubject(params.subject());
        message.setContent(params.content());
        messageRepository.save(message);
    }
}
