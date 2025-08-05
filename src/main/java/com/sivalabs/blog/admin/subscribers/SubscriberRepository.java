package com.sivalabs.blog.admin.subscribers;

import com.sivalabs.blog.shared.entities.Subscriber;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    @Modifying
    @Query(value = "insert into subscribers(email) values(:email) on conflict do nothing", nativeQuery = true)
    void subscribe(String email);

    @Query("select s.email from Subscriber s where s.verified is true")
    List<String> findAllActiveSubscribers();
}
