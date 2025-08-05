package com.sivalabs.blog.blog.domain;

import com.sivalabs.blog.shared.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogMessageRepository extends JpaRepository<Message, Long> {}
