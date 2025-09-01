package com.Tienld.diary_project.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryRequest {
    private String title;
    private String content;
}
