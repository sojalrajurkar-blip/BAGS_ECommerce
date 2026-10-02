package com.rora.backend.cms.service;

import com.rora.backend.cms.dto.*;
import com.rora.backend.common.PagedResponse;

import java.util.List;

public interface CmsService {

    CMSContentDto getHomepageCms();

    CMSContentDto updateHomepageCms(CMSContentDto request, String adminEmail);

    List<JournalArticleDto> getJournalArticles(String category);

    PagedResponse<JournalArticleDto> searchJournalArticles(String category, String query, int page, int size);

    JournalArticleDto getJournalArticleBySlugOrId(String slugOrId);

    JournalArticleDto createJournalArticle(CreateJournalArticleRequest request, String adminEmail);

    JournalArticleDto updateJournalArticle(String id, UpdateJournalArticleRequest request, String adminEmail);

    void deleteJournalArticle(String id, String adminEmail);

    List<FaqCategoryDto> getGroupedFaqs();

    List<FaqItemDto> getAllFaqItems();

    FaqItemDto createFaqItem(CreateFaqItemRequest request, String adminEmail);

    FaqItemDto updateFaqItem(String id, UpdateFaqItemRequest request, String adminEmail);

    void deleteFaqItem(String id, String adminEmail);
}
