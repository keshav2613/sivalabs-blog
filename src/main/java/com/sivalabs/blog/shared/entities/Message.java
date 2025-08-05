package com.sivalabs.blog.shared.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messages_id_gen")
    @SequenceGenerator(name = "messages_id_gen", sequenceName = "message_id_seq", initialValue = 100)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "email", nullable = false, length = 300)
    private String email;

    @Column(name = "subject", nullable = false, length = 300)
    private String subject;

    @Column(name = "content", nullable = false)
    private String content;

    public Message() {}

    public Message(Long id, String name, String email, String subject, String content) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentSummary() {
        if (content == null) {
            return "";
        }
        return content.substring(0, Math.min(content.length(), 100)) + "...";
    }
}
