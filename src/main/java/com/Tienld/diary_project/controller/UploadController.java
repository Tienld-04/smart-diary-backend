package com.Tienld.diary_project.controller;

import com.Tienld.diary_project.dto.response.DiaryResponse;
import com.Tienld.diary_project.entity.DiaryEntity;
import com.Tienld.diary_project.service.CloudinaryService;
import com.Tienld.diary_project.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/images")
public class UploadController {
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private DiaryService  diaryService;

    @PostMapping("/upload")
    public ResponseEntity<Map> uploadImage(@RequestParam("file") MultipartFile file,
                                           @RequestParam(defaultValue = "folder_images")String folder) {
        try {
            Map result = cloudinaryService.uploadFile(file, folder);
            String url = (String) result.get("secure_url");
            String name = (String) result.get("display_name");
            DiaryEntity diary = new DiaryEntity();
//            diary.setName(name);
//            diary.setSecureUrl(url);
            //diaryService.createDiary(diary);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public DiaryResponse getDiaryById(@PathVariable Long id) {

       // return diaryService.getDiaryById(id);
        return null;
    }
}
