package com.Tienld.diary_project.dto.dtos;

import com.Tienld.diary_project.enums.Emotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDTO extends AbstractDTO<DiaryDTO> {
    private Long userId;
    private String title;
    private String content;
    private Emotion emotion;
    private String advice;
    private List<DiaryMediaDTO> mediaDTOS;
    private List<ChatMessageDTO> chatMessageDTOS;

}
