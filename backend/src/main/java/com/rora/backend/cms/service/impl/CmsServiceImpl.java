package com.rora.backend.cms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.cms.dto.*;
import com.rora.backend.cms.entity.CmsContent;
import com.rora.backend.cms.entity.FaqItem;
import com.rora.backend.cms.entity.JournalArticle;
import com.rora.backend.cms.repository.CmsContentRepository;
import com.rora.backend.cms.repository.FaqItemRepository;
import com.rora.backend.cms.repository.JournalArticleRepository;
import com.rora.backend.cms.service.CmsService;
import com.rora.backend.common.PagedResponse;
import com.rora.backend.common.exception.BadRequestException;
import com.rora.backend.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CmsServiceImpl implements CmsService {

    private static final String HOMEPAGE_CONTENT_KEY = "homepage";

    private final CmsContentRepository cmsContentRepository;
    private final JournalArticleRepository journalArticleRepository;
    private final FaqItemRepository faqItemRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public CMSContentDto getHomepageCms() {
        return cmsContentRepository.findByContentKey(HOMEPAGE_CONTENT_KEY)
                .map(this::mapToCmsContentDto)
                .orElseGet(this::getDefaultHomepageCms);
    }

    @Override
    @Transactional
    public CMSContentDto updateHomepageCms(CMSContentDto request, String adminEmail) {
        log.info("Updating homepage CMS content by admin: {}", adminEmail);

        CmsContent content = cmsContentRepository.findByContentKey(HOMEPAGE_CONTENT_KEY)
                .orElse(CmsContent.builder()
                        .contentKey(HOMEPAGE_CONTENT_KEY)
                        .title("Homepage Editorial Content")
                        .build());

        @SuppressWarnings("unchecked")
        Map<String, Object> contentMap = objectMapper.convertValue(request, Map.class);
        content.setContentData(contentMap);
        content.setTitle("Homepage Editorial Content");
        CmsContent saved = cmsContentRepository.save(content);

        return mapToCmsContentDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JournalArticleDto> getJournalArticles(String category) {
        List<JournalArticle> articles;
        if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("all")) {
            articles = journalArticleRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc(category.trim());
        } else {
            articles = journalArticleRepository.findAllByOrderByCreatedAtDesc();
        }
        return articles.stream().map(this::mapToJournalArticleDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JournalArticleDto> searchJournalArticles(String category, String query, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<JournalArticle> articlePage = journalArticleRepository.searchArticles(category, query, pageable);

        List<JournalArticleDto> content = articlePage.getContent().stream()
                .map(this::mapToJournalArticleDto)
                .collect(Collectors.toList());

        return PagedResponse.<JournalArticleDto>builder()
                .content(content)
                .page(articlePage.getNumber())
                .size(articlePage.getSize())
                .totalElements(articlePage.getTotalElements())
                .totalPages(articlePage.getTotalPages())
                .last(articlePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public JournalArticleDto getJournalArticleBySlugOrId(String slugOrId) {
        JournalArticle article = journalArticleRepository.findBySlug(slugOrId)
                .or(() -> journalArticleRepository.findById(slugOrId))
                .orElseThrow(() -> new ResourceNotFoundException("Journal article not found with slug or ID: " + slugOrId));
        return mapToJournalArticleDto(article);
    }

    @Override
    @Transactional
    public JournalArticleDto createJournalArticle(CreateJournalArticleRequest request, String adminEmail) {
        log.info("Creating journal article '{}' by admin: {}", request.getTitle(), adminEmail);

        String slug = (request.getSlug() != null && !request.getSlug().trim().isEmpty())
                ? generateSlug(request.getSlug())
                : generateSlug(request.getTitle());

        if (journalArticleRepository.existsBySlug(slug)) {
            slug = slug + "-" + UUID.randomUUID().toString().substring(0, 6);
        }

        JournalArticle article = JournalArticle.builder()
                .slug(slug)
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .category(request.getCategory())
                .readTime(request.getReadTime())
                .author(request.getAuthor() != null ? request.getAuthor() : "RÓRA Editorial")
                .image(request.getImage())
                .excerpt(request.getExcerpt())
                .content(request.getContent())
                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
                .publishedAt(request.getPublishedAt() != null ? request.getPublishedAt() : "Recent")
                .build();

        JournalArticle saved = journalArticleRepository.save(article);
        return mapToJournalArticleDto(saved);
    }

    @Override
    @Transactional
    public JournalArticleDto updateJournalArticle(String id, UpdateJournalArticleRequest request, String adminEmail) {
        log.info("Updating journal article ID: {} by admin: {}", id, adminEmail);

        JournalArticle article = journalArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal article not found with ID: " + id));

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            article.setTitle(request.getTitle());
        }
        if (request.getSubtitle() != null) {
            article.setSubtitle(request.getSubtitle());
        }
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            article.setCategory(request.getCategory());
        }
        if (request.getReadTime() != null && !request.getReadTime().trim().isEmpty()) {
            article.setReadTime(request.getReadTime());
        }
        if (request.getAuthor() != null) {
            article.setAuthor(request.getAuthor());
        }
        if (request.getImage() != null && !request.getImage().trim().isEmpty()) {
            article.setImage(request.getImage());
        }
        if (request.getExcerpt() != null && !request.getExcerpt().trim().isEmpty()) {
            article.setExcerpt(request.getExcerpt());
        }
        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            article.setContent(request.getContent());
        }
        if (request.getTags() != null) {
            article.setTags(request.getTags());
        }
        if (request.getPublishedAt() != null) {
            article.setPublishedAt(request.getPublishedAt());
        }
        if (request.getSlug() != null && !request.getSlug().trim().isEmpty() && !request.getSlug().equals(article.getSlug())) {
            String newSlug = generateSlug(request.getSlug());
            if (!newSlug.equals(article.getSlug()) && journalArticleRepository.existsBySlug(newSlug)) {
                throw new BadRequestException("Article with slug '" + newSlug + "' already exists");
            }
            article.setSlug(newSlug);
        }

        JournalArticle updated = journalArticleRepository.save(article);
        return mapToJournalArticleDto(updated);
    }

    @Override
    @Transactional
    public void deleteJournalArticle(String id, String adminEmail) {
        log.info("Deleting journal article ID: {} by admin: {}", id, adminEmail);
        JournalArticle article = journalArticleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal article not found with ID: " + id));
        journalArticleRepository.delete(article);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaqCategoryDto> getGroupedFaqs() {
        List<FaqItem> activeItems = faqItemRepository.findByIsActiveTrueOrderByCategoryAscDisplayOrderAsc();

        Map<String, List<FaqItemDto>> grouped = activeItems.stream()
                .map(this::mapToFaqItemDto)
                .collect(Collectors.groupingBy(
                        FaqItemDto::getCategory,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return grouped.entrySet().stream()
                .map(entry -> FaqCategoryDto.builder()
                        .category(entry.getKey())
                        .items(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaqItemDto> getAllFaqItems() {
        return faqItemRepository.findAllByOrderByCategoryAscDisplayOrderAsc().stream()
                .map(this::mapToFaqItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FaqItemDto createFaqItem(CreateFaqItemRequest request, String adminEmail) {
        log.info("Creating FAQ item for category '{}' by admin: {}", request.getCategory(), adminEmail);

        FaqItem item = FaqItem.builder()
                .category(request.getCategory().trim())
                .question(request.getQuestion().trim())
                .answer(request.getAnswer().trim())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        FaqItem saved = faqItemRepository.save(item);
        return mapToFaqItemDto(saved);
    }

    @Override
    @Transactional
    public FaqItemDto updateFaqItem(String id, UpdateFaqItemRequest request, String adminEmail) {
        log.info("Updating FAQ item ID: {} by admin: {}", id, adminEmail);

        FaqItem item = faqItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ item not found with ID: " + id));

        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            item.setCategory(request.getCategory().trim());
        }
        if (request.getQuestion() != null && !request.getQuestion().trim().isEmpty()) {
            item.setQuestion(request.getQuestion().trim());
        }
        if (request.getAnswer() != null && !request.getAnswer().trim().isEmpty()) {
            item.setAnswer(request.getAnswer().trim());
        }
        if (request.getDisplayOrder() != null) {
            item.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsActive() != null) {
            item.setIsActive(request.getIsActive());
        }

        FaqItem updated = faqItemRepository.save(item);
        return mapToFaqItemDto(updated);
    }

    @Override
    @Transactional
    public void deleteFaqItem(String id, String adminEmail) {
        log.info("Deleting FAQ item ID: {} by admin: {}", id, adminEmail);
        FaqItem item = faqItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ item not found with ID: " + id));
        faqItemRepository.delete(item);
    }

    // --- Helper Mappers ---

    private CMSContentDto mapToCmsContentDto(CmsContent content) {
        try {
            if (content.getContentData() != null) {
                return objectMapper.convertValue(content.getContentData(), CMSContentDto.class);
            }
        } catch (Exception e) {
            log.error("Failed to parse CMS content data JSON: {}", e.getMessage());
        }
        return getDefaultHomepageCms();
    }

    private CMSContentDto getDefaultHomepageCms() {
        return CMSContentDto.builder()
                .announcementBar(AnnouncementBarDto.builder()
                        .enabled(true)
                        .text("Complimentary shipping across India on orders over ₹1,999 • Handcrafted with certified recycled textiles")
                        .link("/categories/all")
                        .build())
                .heroBanner(HeroBannerDto.builder()
                        .eyebrow("Architectural Carry • Autumn 2026")
                        .title("Engineered for the Modern Journey")
                        .subtitle("Tactile Japanese recycled nylon and full-grain Italian leather, crafted in small artisanal batches with zero compromise.")
                        .primaryButtonText("Explore Collection")
                        .secondaryButtonText("Read the Journal")
                        .build())
                .craftsmanshipFeature(CraftsmanshipFeatureDto.builder()
                        .heading("Architectural Restraint Meets Uncompromising Craft")
                        .paragraph1("Every RÓRA silhouette begins as a mathematical exercise in volume, balance, and tactile reduction. We eliminate unnecessary ornamentation to let premium materials and ergonomic geometry shine.")
                        .paragraph2("Hand-burnished leather edges, custom anodized matte hardware, and weatherproof stormproof zippers ensure a lifetime of faithful companion carry.")
                        .build())
                .build();
    }

    private JournalArticleDto mapToJournalArticleDto(JournalArticle article) {
        return JournalArticleDto.builder()
                .id(article.getId())
                .slug(article.getSlug())
                .title(article.getTitle())
                .subtitle(article.getSubtitle())
                .category(article.getCategory())
                .readTime(article.getReadTime())
                .author(article.getAuthor())
                .image(article.getImage())
                .excerpt(article.getExcerpt())
                .content(article.getContent())
                .tags(article.getTags())
                .publishedAt(article.getPublishedAt())
                .date(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }

    private FaqItemDto mapToFaqItemDto(FaqItem item) {
        return FaqItemDto.builder()
                .id(item.getId())
                .category(item.getCategory())
                .question(item.getQuestion())
                .answer(item.getAnswer())
                .q(item.getQuestion())
                .a(item.getAnswer())
                .displayOrder(item.getDisplayOrder())
                .isActive(item.getIsActive())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    private String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "article-" + System.currentTimeMillis();
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
