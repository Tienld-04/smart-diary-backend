package com.Tienld.diary_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage extends BaseEntity{

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "is_user_message", nullable = false)
    private boolean isUserMessage;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private DiaryEntity diary;
}
