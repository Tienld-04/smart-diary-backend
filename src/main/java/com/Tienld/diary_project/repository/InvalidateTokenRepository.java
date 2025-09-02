package com.Tienld.diary_project.repository;

import com.Tienld.diary_project.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidateTokenRepository extends JpaRepository<InvalidatedToken, Long> {
    boolean existsByJti(String jti);
}
