package com.Tienld.diary_project.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDiaryRequest {
    private String title;
    private String content;

    // Danh sách URL ảnh muốn giữ lại
    private List<String> keepMediaUrls;

    // Danh sách URL ảnh muốn xóa
    private List<String> deleteMediaUrls;

    // Danh sách file ảnh mới upload
    private List<MultipartFile> newMedias;


}
