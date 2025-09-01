package com.Tienld.diary_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "diary_media")
public class DiaryMedia extends BaseEntity {

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    private String caption;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    private DiaryEntity diary;
}
