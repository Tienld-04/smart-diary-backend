package com.Tienld.diary_project.repository;

import com.Tienld.diary_project.entity.DiaryEntity;
import com.Tienld.diary_project.entity.DiaryMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryMediaRepository extends JpaRepository<DiaryMedia, Long> {
    List<DiaryMedia> findByDiary(DiaryEntity diary);

    List<DiaryMedia> findByDiary_Id(Long diaryId);

    void deleteByDiary(DiaryEntity diary);

    void deleteByDiary_Id(Long diaryId);

    // Tìm danh sách media theo URL
    List<DiaryMedia> findAllByMediaUrlIn(List<String> mediaUrls);

    //List<DiaryMedia> findByDiaryId(Long diaryId);
    // Nếu cần xóa 1 ảnh theo URL
    void deleteByMediaUrl(String mediaUrl);
}
