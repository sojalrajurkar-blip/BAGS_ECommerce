package com.rora.backend.cms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.cms.dto.*;
import com.rora.backend.cms.entity.CmsContent;
import com.rora.backend.cms.entity.FaqItem;
import com.rora.backend.cms.entity.JournalArticle;
import com.rora.backend.cms.repository.CmsContentRepository;
import com.rora.backend.cms.repository.FaqItemRepository;
import com.rora.backend.cms.repository.JournalArticleRepository;
import com.rora.backend.cms.service.impl.CmsServiceImpl;
import com.rora.backend.common.PagedResponse;
import com.rora.backend.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CmsServiceTest {

    @Mock
    private CmsContentRepository cmsContentRepository;

    @Mock
    private JournalArticleRepository journalArticleRepository;

    @Mock
    private FaqItemRepository faqItemRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CmsServiceImpl cmsService;

    private JournalArticle mockArticle;
    private FaqItem mockFaq;

    @BeforeEach
    void setUp() {
        mockArticle = JournalArticle.builder()
                .id("art-1")
                .slug("5-must-have-features-in-a-travel-bag")
                .title("5 Must-Have Features in an Intentional Travel Bag")
                .subtitle("Geometry and materials")
                .category("Travel & Mobility")
                .readTime("5 min read")
                .author("Søren Lindqvist")
                .image("https://images.unsplash.com/photo-1520006403909-838d6b92c22e")
                .excerpt("Navigating transit")
                .content("Full story content")
                .tags(List.of("Travel", "Design"))
                .publishedAt("April 25, 2026")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        mockFaq = FaqItem.builder()
                .id("faq-1")
                .category("General & Craftsmanship")
                .question("Where are RÓRA bags designed?")
                .answer("Our design studio is in Copenhagen.")
                .displayOrder(1)
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should return default homepage CMS when no record in database")
    void testGetHomepageCms_Default() {
        when(cmsContentRepository.findByContentKey("homepage")).thenReturn(Optional.empty());

        CMSContentDto result = cmsService.getHomepageCms();

        assertThat(result).isNotNull();
        assertThat(result.getAnnouncementBar()).isNotNull();
        assertThat(result.getHeroBanner().getTitle()).contains("Engineered for the Modern Journey");
        assertThat(result.getCraftsmanshipFeature().getHeading()).contains("Architectural Restraint");
    }

    @Test
    @DisplayName("Should update homepage CMS content")
    void testUpdateHomepageCms() {
        CMSContentDto updateReq = CMSContentDto.builder()
                .announcementBar(AnnouncementBarDto.builder().enabled(true).text("New Announcement").build())
                .heroBanner(HeroBannerDto.builder().title("New Hero Title").build())
                .build();

        when(cmsContentRepository.findByContentKey("homepage")).thenReturn(Optional.empty());
        when(cmsContentRepository.save(any(CmsContent.class))).thenAnswer(inv -> inv.getArgument(0));

        CMSContentDto result = cmsService.updateHomepageCms(updateReq, "admin@rora.com");

        assertThat(result).isNotNull();
        assertThat(result.getHeroBanner().getTitle()).isEqualTo("New Hero Title");
        verify(cmsContentRepository).save(any(CmsContent.class));
    }

    @Test
    @DisplayName("Should list journal articles with category filter")
    void testGetJournalArticles() {
        when(journalArticleRepository.findByCategoryIgnoreCaseOrderByCreatedAtDesc("Travel & Mobility"))
                .thenReturn(List.of(mockArticle));

        List<JournalArticleDto> articles = cmsService.getJournalArticles("Travel & Mobility");

        assertThat(articles).hasSize(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(mockArticle.getTitle());
    }

    @Test
    @DisplayName("Should search journal articles with pagination")
    void testSearchJournalArticles() {
        Page<JournalArticle> page = new PageImpl<>(List.of(mockArticle));
        when(journalArticleRepository.searchArticles(eq("all"), eq("travel"), any(Pageable.class)))
                .thenReturn(page);

        PagedResponse<JournalArticleDto> response = cmsService.searchJournalArticles("all", "travel", 0, 10);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should get journal article by slug or ID")
    void testGetJournalArticleBySlugOrId_Found() {
        when(journalArticleRepository.findBySlug("5-must-have-features-in-a-travel-bag"))
                .thenReturn(Optional.of(mockArticle));

        JournalArticleDto dto = cmsService.getJournalArticleBySlugOrId("5-must-have-features-in-a-travel-bag");

        assertThat(dto).isNotNull();
        assertThat(dto.getSlug()).isEqualTo("5-must-have-features-in-a-travel-bag");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when article not found")
    void testGetJournalArticleBySlugOrId_NotFound() {
        when(journalArticleRepository.findBySlug("unknown-slug")).thenReturn(Optional.empty());
        when(journalArticleRepository.findById("unknown-slug")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cmsService.getJournalArticleBySlugOrId("unknown-slug"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create new journal article with auto slug generation")
    void testCreateJournalArticle() {
        CreateJournalArticleRequest req = CreateJournalArticleRequest.builder()
                .title("The Architectural Backpack")
                .category("Design")
                .readTime("3 min read")
                .image("https://example.com/image.jpg")
                .excerpt("A study in minimalism")
                .content("Detailed breakdown of geometry")
                .build();

        when(journalArticleRepository.existsBySlug(anyString())).thenReturn(false);
        when(journalArticleRepository.save(any(JournalArticle.class))).thenAnswer(inv -> {
            JournalArticle a = inv.getArgument(0);
            a.setId("art-new");
            return a;
        });

        JournalArticleDto created = cmsService.createJournalArticle(req, "editor@rora.com");

        assertThat(created).isNotNull();
        assertThat(created.getSlug()).isEqualTo("the-architectural-backpack");
        assertThat(created.getTitle()).isEqualTo("The Architectural Backpack");
    }

    @Test
    @DisplayName("Should group active FAQs by category")
    void testGetGroupedFaqs() {
        when(faqItemRepository.findByIsActiveTrueOrderByCategoryAscDisplayOrderAsc())
                .thenReturn(List.of(mockFaq));

        List<FaqCategoryDto> categories = cmsService.getGroupedFaqs();

        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getCategory()).isEqualTo("General & Craftsmanship");
        assertThat(categories.get(0).getItems()).hasSize(1);
        assertThat(categories.get(0).getItems().get(0).getQuestion()).isEqualTo(mockFaq.getQuestion());
    }

    @Test
    @DisplayName("Should create new FAQ item")
    void testCreateFaqItem() {
        CreateFaqItemRequest req = CreateFaqItemRequest.builder()
                .category("Shipping")
                .question("Do you ship internationally?")
                .answer("Yes, we do.")
                .displayOrder(1)
                .isActive(true)
                .build();

        when(faqItemRepository.save(any(FaqItem.class))).thenAnswer(inv -> {
            FaqItem f = inv.getArgument(0);
            f.setId("faq-new");
            return f;
        });

        FaqItemDto created = cmsService.createFaqItem(req, "admin@rora.com");

        assertThat(created).isNotNull();
        assertThat(created.getQuestion()).isEqualTo("Do you ship internationally?");
    }
}
