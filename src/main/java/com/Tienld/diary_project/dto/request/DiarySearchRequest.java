package com.Tienld.diary_project.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiarySearchRequest {
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
}
