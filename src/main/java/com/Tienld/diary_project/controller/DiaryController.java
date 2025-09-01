package com.Tienld.diary_project.controller;

import com.Tienld.diary_project.dto.request.DiaryRequest;
import com.Tienld.diary_project.dto.request.DiarySearchRequest;
import com.Tienld.diary_project.dto.request.UpdateDiaryRequest;
import com.Tienld.diary_project.dto.response.DiaryResponse;
import com.Tienld.diary_project.service.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/diaries")
public class DiaryController {
    @Autowired
    private DiaryService diaryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiaryResponse> createDiary(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
//            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {

        DiaryRequest meta = new DiaryRequest();
        meta.setTitle(title);
        meta.setContent(content);
        DiaryResponse result = diaryService.createDiaryWithMedia(meta, images);
        return ResponseEntity.ok(result);
    }

    //    @PutMapping(value = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<DiaryResponse> updateDiary(
//            @PathVariable Long id,
//            @RequestPart("request") UpdateDiaryRequest request,   // JSON
//            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages // File ảnh
//    ) throws IOException {
//        System.out.println(id);
//        DiaryResponse response = diaryService.updateDiary(id, request, newImages);
//        return ResponseEntity.ok(response);
//    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DiaryResponse> updateDiary(
            @PathVariable Long id,
            @RequestParam("request") String requestJson,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
    ) throws IOException {
        // Parse JSON thủ công
        ObjectMapper mapper = new ObjectMapper();
        UpdateDiaryRequest request = mapper.readValue(requestJson, UpdateDiaryRequest.class);

        DiaryResponse response = diaryService.updateDiary(id, request, newImages);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DiaryResponse>> getAllDiaries() {
        List<DiaryResponse> diaries = diaryService.getAllDiaryByUserCurrent();
        return ResponseEntity.ok(diaries);
    }

    @GetMapping("/diaryId/{id}")
    public ResponseEntity<DiaryResponse> getDiaryById(@PathVariable Long id) {
        DiaryResponse diary = diaryService.findDiaryById(id);
        return ResponseEntity.ok(diary);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<DiaryResponse>> getThreeRecentDiary() {
        List<DiaryResponse> diaryResponses = diaryService.getRecentDiary();
        return ResponseEntity.ok(diaryResponses);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<DiaryResponse>> getAllDiariesByUser_Id(@PathVariable Long userId) {
//        userId = 1L;
        List<DiaryResponse> diaries = diaryService.findByUser_Id(userId);
        return ResponseEntity.ok(diaries);
    }

    @GetMapping("/search")
    ResponseEntity<List<DiaryResponse>> searchDiaryByDate(@RequestParam("formDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                                          @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate
    ) {
        DiarySearchRequest diarySearchRequest = new DiarySearchRequest();
        diarySearchRequest.setFromDate(fromDate);
        diarySearchRequest.setToDate(toDate);
        List<DiaryResponse> result = diaryService.searchDiaryByDate(diarySearchRequest);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Map<String, String>> deleteDiaryByIds(@PathVariable List<Long> diaryId) {
        diaryService.deleteDiaryByIds(diaryId);
        Map<String, String> response = new HashMap<>();
        response.put("status", "đã xóa thành công");
        return ResponseEntity.ok(response);
    }

}
