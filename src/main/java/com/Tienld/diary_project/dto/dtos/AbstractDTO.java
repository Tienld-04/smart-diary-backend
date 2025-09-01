package com.Tienld.diary_project.dto.dtos;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class AbstractDTO<T> implements Serializable {

    private static final long serialVersionUID = 7213600440729202783L;
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

}
