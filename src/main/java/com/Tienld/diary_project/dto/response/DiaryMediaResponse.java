package com.Tienld.diary_project.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryMediaResponse {
    private String caption;
    private String mediaUrl;

}
