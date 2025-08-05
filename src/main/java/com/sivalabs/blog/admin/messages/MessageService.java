package com.sivalabs.blog.admin.messages;

import static com.sivalabs.blog.shared.models.PagedResult.getPagedResult;
import static com.sivalabs.blog.shared.utils.CommonUtils.isValidIdList;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.shared.entities.Message;
import com.sivalabs.blog.shared.models.PagedResult;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ApplicationProperties properties;

    public MessageService(MessageRepository messageRepository, ApplicationProperties properties) {
        this.messageRepository = messageRepository;
        this.properties = properties;
    }

    @Transactional
    public void deleteMessages(List<Long> messageIds) {
        if (isValidIdList(messageIds)) {
            messageRepository.deleteAllById(messageIds);
        }
    }

    @Transactional(readOnly = true)
    public PagedResult<Message> getMessages(Integer pageNo) {
        return getPagedResult(
                pageNo, properties.adminDefaultPageSize(), messageRepository::findAll, message -> message);
    }
}
