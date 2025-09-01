package com.Tienld.diary_project.dto.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO extends AbstractDTO<ChatMessageDTO> {
    private String message;
    private boolean isUserMessage;
}
