package com.textforge.backend.repository;

import com.textforge.backend.entity.Generation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenerationRepository extends JpaRepository<Generation, Long> {
    List<Generation> findByUserIdOrderByCreatedAtDesc(Long userId);
}
