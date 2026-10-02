package com.rora.backend.cms.repository;

import com.rora.backend.cms.entity.FaqItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqItemRepository extends JpaRepository<FaqItem, String> {

    List<FaqItem> findByIsActiveTrueOrderByCategoryAscDisplayOrderAsc();

    List<FaqItem> findAllByOrderByCategoryAscDisplayOrderAsc();

    List<FaqItem> findByCategoryIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String category);
}
