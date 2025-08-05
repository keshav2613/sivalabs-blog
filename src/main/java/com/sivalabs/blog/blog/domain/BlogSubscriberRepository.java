package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.shared.entities.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BlogSubscriberRepository extends JpaRepository<Subscriber, Long> {
    @Modifying
    @Query(value = "insert into subscribers(email) values(:email) on conflict do nothing", nativeQuery = true)
    void subscribe(String email);
}
