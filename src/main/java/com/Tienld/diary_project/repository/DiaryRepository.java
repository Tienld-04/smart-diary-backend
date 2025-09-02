package com.Tienld.diary_project.repository;

import com.Tienld.diary_project.entity.DiaryEntity;
import com.Tienld.diary_project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity, Long> {
    List<DiaryEntity> findByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);

    List<DiaryEntity> findByUser_UsernameAndCreatedAtBetween(String username, LocalDateTime fromDate, LocalDateTime toDate);

    List<DiaryEntity> findTop3ByUser_UsernameOrderByCreatedAtDesc(String username);

    List<DiaryEntity> findByUser(UserEntity user);

    List<DiaryEntity> findByUser_Id(Long userId);

    void deleteByIdIn(List<Long> ids);
}
