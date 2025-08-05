package com.sivalabs.blog.admin.subscribers;

import static com.sivalabs.blog.shared.models.PagedResult.getPagedResult;
import static com.sivalabs.blog.shared.utils.CommonUtils.isValidIdList;

import com.sivalabs.blog.ApplicationProperties;
import com.sivalabs.blog.shared.entities.Subscriber;
import com.sivalabs.blog.shared.models.PagedResult;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final ApplicationProperties properties;

    public SubscriberService(SubscriberRepository subscriberRepository, ApplicationProperties properties) {
        this.subscriberRepository = subscriberRepository;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public List<String> getAllActiveSubscribers() {
        return subscriberRepository.findAllActiveSubscribers();
    }

    @Transactional(readOnly = true)
    public PagedResult<Subscriber> getSubscribers(int pageNo) {
        return getPagedResult(
                pageNo, properties.adminDefaultPageSize(), subscriberRepository::findAll, subscriber -> subscriber);
    }

    @Transactional
    public void deleteSubscribers(List<Long> subscriberIds) {
        if (isValidIdList(subscriberIds)) {
            subscriberRepository.deleteAllById(subscriberIds);
        }
    }
}
