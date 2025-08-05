package com.sivalabs.blog.admin.messages;

import com.sivalabs.blog.shared.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {}
