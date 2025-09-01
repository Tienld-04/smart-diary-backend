package com.Tienld.diary_project.service;

import com.Tienld.diary_project.converter.DiaryConverter;
import com.Tienld.diary_project.dto.request.DiaryRequest;
import com.Tienld.diary_project.dto.request.DiarySearchRequest;
import com.Tienld.diary_project.dto.request.UpdateDiaryRequest;
import com.Tienld.diary_project.dto.response.DiaryResponse;
import com.Tienld.diary_project.entity.DiaryEntity;
import com.Tienld.diary_project.entity.DiaryMedia;
import com.Tienld.diary_project.entity.UserEntity;
import com.Tienld.diary_project.enums.Emotion;
import com.Tienld.diary_project.repository.DiaryMediaRepository;
import com.Tienld.diary_project.repository.DiaryRepository;
import com.Tienld.diary_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DiaryService {
    @Autowired private DiaryRepository diaryRepository;
    @Autowired private GeminiAPIService geminiAPIService;
    @Autowired private CloudinaryService cloudinaryService;
    @Autowired private DiaryMediaRepository diaryMediaRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DiaryConverter diaryConverter;

    @Transactional
    public DiaryResponse createDiaryWithMedia(DiaryRequest req, List<MultipartFile> images) throws IOException {
        // Dự đoán cảm xúc từ content (đã chứa cả text + icon)
        Emotion emotion = geminiAPIService.predictTextEmotion(req.getContent(), null); // icon = null
        // Sinh lời khuyên
        String advice = geminiAPIService.generateAdvice(req.getContent(), emotion);

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        UserEntity userCurrent = userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("Username not found"));
        // Lưu Diary
        DiaryEntity diary = DiaryEntity.builder()
                .title(req.getTitle())
                .content(req.getContent()) // Lưu nguyên content chứa text + icon
                .emotion(emotion)
                .advice(advice)
                .user(userCurrent)
                .build();
        diary = diaryRepository.save(diary);

        // Upload ảnh và lưu DiaryMedia
        List<String> mediaUrls = new ArrayList<>();
        if (images != null) {
            for (MultipartFile img : images) {
                Map up = cloudinaryService.uploadFile(img, "diary_images");
                String url = (String) up.get("secure_url");
                mediaUrls.add(url);

                DiaryMedia media = DiaryMedia.builder()
                        .mediaUrl(url)
                        .diary(diary)
                        .build();
                diaryMediaRepository.save(media);
            }
        }

        // Build response
        DiaryResponse res = DiaryResponse.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent()) // Trả về nguyên content chứa text + icon
                .emotion(diary.getEmotion())
                .advice(diary.getAdvice())
                .mediaUrls(mediaUrls)
                .build();

        return res;
    }

    public DiaryResponse findDiaryById(Long id) {
        DiaryEntity diary = diaryRepository.findById(id).get();
        DiaryResponse diaryResponse = diaryConverter.converToDiaryResponse(diary);
        return diaryResponse;
    }

    @Transactional
    public DiaryResponse updateDiary(Long id, UpdateDiaryRequest req, List<MultipartFile> newImages) throws IOException {
        DiaryEntity diary = diaryRepository.findById(id).orElseThrow(() -> new RuntimeException("Diary not found with id: " + id));
        diary.setTitle(req.getTitle());
        diary.setContent(req.getContent());
        Emotion emotion = geminiAPIService.predictTextEmotion(req.getContent(), null);
        String advice = geminiAPIService.generateAdvice(req.getContent(), emotion);
        diary.setEmotion(emotion);
        diary.setAdvice(advice);
        List<String> mediaUrls = new ArrayList<>();
        // Xóa ảnh theo req.getDeleteMediaUrls()
        if(req.getDeleteMediaUrls() != null &&  !req.getDeleteMediaUrls().isEmpty()) {
            List<DiaryMedia> mediasToDelete = diaryMediaRepository.findAllByMediaUrlIn(req.getDeleteMediaUrls());
            for (DiaryMedia media : mediasToDelete) {
                diaryMediaRepository.delete(media);
            }
        }
        // lấy danh sách Media còn lại sau khi xóa
        List<DiaryMedia>  mediasRemaining = diaryMediaRepository.findByDiary_Id(diary.getId());
        for (DiaryMedia media : mediasRemaining) {
            mediaUrls.add(media.getMediaUrl());
        }
        // Up imgage mới
        if(newImages != null){
            for (MultipartFile img : newImages) {
                Map uploadResult = cloudinaryService.uploadFile(img, "diary_images");
                String url = (String) uploadResult.get("secure_url");
                mediaUrls.add(url);
                DiaryMedia media = DiaryMedia.builder()
                        .mediaUrl(url)
                        .diary(diary)
                        .build();
                diaryMediaRepository.save(media);
            }
        }
        diaryRepository.save(diary);

        return DiaryResponse.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .emotion(diary.getEmotion())
                .advice(diary.getAdvice())
                .mediaUrls(mediaUrls)
                .build();
    }
    @Transactional
    public void deleteDiaryByIds(List<Long> ids) {
        if(ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("List id diary null or empty");
        }
        diaryRepository.deleteByIdIn(ids);
    }

    public List<DiaryResponse> getAllDiaryByUserCurrent() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<DiaryEntity> diaryEntityList = diaryRepository.findByUser(user);
        List<DiaryResponse> res = new ArrayList<>();
        for (DiaryEntity diaryEntity : diaryEntityList) {
            DiaryResponse diaryResponse = diaryConverter.converToDiaryResponse(diaryEntity);
            res.add(diaryResponse);
        }
        return res;
    }
    //@PostFilter("filterObject.username == authentication.name")
    public List<DiaryResponse> searchDiaryByDate(DiarySearchRequest diarySearchRequest){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime formDate = diarySearchRequest.getFromDate();
        LocalDateTime toDate = diarySearchRequest.getToDate();
        List<DiaryEntity> diaryEntityList = diaryRepository.findByUser_UsernameAndCreatedAtBetween(username, formDate, toDate);
        List<DiaryResponse> res = new ArrayList<>();
        for(DiaryEntity diaryEntity : diaryEntityList){
            DiaryResponse diaryResponse = diaryConverter.converToDiaryResponse2(diaryEntity);
            res.add(diaryResponse);
        }

        return res;
    }

    public List<DiaryResponse> findByUser_Id(Long userId) {
        List<DiaryEntity> diaryEntities = diaryRepository.findByUser_Id(userId);
        List<DiaryResponse> res = new ArrayList<>();
        for (DiaryEntity diaryEntity : diaryEntities) {
            DiaryResponse diaryResponse = diaryConverter.converToDiaryResponse(diaryEntity);
            res.add(diaryResponse);
        }
        return res;
    }
}