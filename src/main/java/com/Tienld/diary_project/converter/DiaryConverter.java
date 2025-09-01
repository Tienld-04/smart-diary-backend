package com.Tienld.diary_project.converter;

import com.Tienld.diary_project.dto.response.DiaryMediaResponse;
import com.Tienld.diary_project.dto.response.DiaryResponse;
import com.Tienld.diary_project.entity.DiaryEntity;
import com.Tienld.diary_project.entity.DiaryMedia;
import com.Tienld.diary_project.service.DiaryMediaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DiaryConverter {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private DiaryMediaService diaryMediaService;

    public DiaryEntity converToDiaryEntity(DiaryResponse diaryResponse) {
        DiaryEntity diary = modelMapper.map(diaryResponse, DiaryEntity.class);
        return  diary;
    }

    public DiaryResponse converToDiaryResponse(DiaryEntity diaryEntity) {
        DiaryResponse diaryResponse = modelMapper.map(diaryEntity, DiaryResponse.class);
        List<DiaryMedia> diaryMediaList = diaryEntity.getMedia();
        List<String> imgUrl = new ArrayList<>();
        for(DiaryMedia diaryMedia : diaryMediaList){
            imgUrl.add(diaryMedia.getMediaUrl());
        }
        diaryResponse.setMediaUrls(imgUrl);
        return diaryResponse;

    }

    public DiaryResponse converToDiaryResponse2(DiaryEntity diaryEntity) {
        DiaryResponse diaryResponse = modelMapper.map(diaryEntity, DiaryResponse.class);
        List<DiaryMediaResponse> diaryMediaList = diaryMediaService.getAllDiaryMediaByDiaryId(diaryEntity.getId());
        List<String> imgUrl = new ArrayList<>();
        for(DiaryMediaResponse diaryMediaResponse : diaryMediaList){
            imgUrl.add(diaryMediaResponse.getMediaUrl());
        }
        diaryResponse.setMediaUrls(imgUrl);
        return diaryResponse;

    }
}
