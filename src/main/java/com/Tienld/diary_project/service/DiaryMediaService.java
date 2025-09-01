package com.Tienld.diary_project.service;

import com.Tienld.diary_project.dto.response.DiaryMediaResponse;
import com.Tienld.diary_project.entity.DiaryMedia;
import com.Tienld.diary_project.repository.DiaryMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiaryMediaService {
    @Autowired
    private DiaryMediaRepository diaryMediaRepository;

    public List<DiaryMediaResponse> getAllDiaryMedia() {
        List<DiaryMedia> diaryMediaList = diaryMediaRepository.findAll();
        List<DiaryMediaResponse> res = new ArrayList<>();
        for(DiaryMedia diaryMedia : diaryMediaList) {
            DiaryMediaResponse diaryMediaResponse = new DiaryMediaResponse();
            diaryMediaResponse.setCaption(diaryMedia.getCaption());
            diaryMediaResponse.setMediaUrl(diaryMedia.getMediaUrl());
            res.add(diaryMediaResponse);
        }
        return res;
    }

    public List<DiaryMediaResponse> getAllDiaryMediaByDiaryId(Long diaryId) {
        List<DiaryMedia> diaryMediaList = diaryMediaRepository.findByDiary_Id(diaryId);
        List<DiaryMediaResponse> res = new ArrayList<>();
        for(DiaryMedia diaryMedia : diaryMediaList) {
            DiaryMediaResponse diaryMediaResponse = new DiaryMediaResponse();
            diaryMediaResponse.setCaption(diaryMedia.getCaption());
            diaryMediaResponse.setMediaUrl(diaryMedia.getMediaUrl());
            res.add(diaryMediaResponse);
        }
        return res;

    }

    public void deleteByDiary_Id(Long diaryId) {
        diaryMediaRepository.deleteByDiary_Id(diaryId);
    }
}
