package com.Tienld.diary_project.dto.response;

import com.Tienld.diary_project.enums.Emotion;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryResponse {
    private Long id;
    private String title;           
    private String content;         
    private Emotion emotion;        
    private String advice;
    private List<String> mediaUrls; 
}
