package com.rora.backend.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rora.backend.cms.dto.*;
import com.rora.backend.cms.entity.CmsContent;
import com.rora.backend.cms.entity.FaqItem;
import com.rora.backend.cms.entity.JournalArticle;
import com.rora.backend.cms.repository.CmsContentRepository;
import com.rora.backend.cms.repository.FaqItemRepository;
import com.rora.backend.cms.repository.JournalArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CmsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CmsContentRepository cmsContentRepository;

    @Autowired
    private JournalArticleRepository journalArticleRepository;

    @Autowired
    private FaqItemRepository faqItemRepository;

    private JournalArticle testArticle;
    private FaqItem testFaq;

    @BeforeEach
    void setUp() {
        journalArticleRepository.deleteAll();
        faqItemRepository.deleteAll();

        testArticle = journalArticleRepository.save(JournalArticle.builder()
                .slug("travel-in-the-nordics")
                .title("Travel in the Nordics: Minimalist Guide")
                .subtitle("Pack lighter, travel farther")
                .category("Travel & Mobility")
                .readTime("4 min read")
                .author("Elena Rostova")
                .image("https://images.unsplash.com/photo-1520006403909-838d6b92c22e")
                .excerpt("A study of northern transit aesthetics.")
                .content("Nordic architecture prioritizes clean lines and intentional functionality...")
                .tags(List.of("Travel", "Nordic", "Minimalism"))
                .publishedAt("May 10, 2026")
                .build());

        testFaq = faqItemRepository.save(FaqItem.builder()
                .category("Materials & Craft")
                .question("What leather grade do you use?")
                .answer("Full-grain Tuscan vegetable-tanned leather.")
                .displayOrder(1)
                .isActive(true)
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/cms/content - Should return public homepage CMS content")
    void testGetHomepageContent() throws Exception {
        mockMvc.perform(get("/api/v1/cms/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.heroBanner").exists())
                .andExpect(jsonPath("$.data.announcementBar").exists())
                .andExpect(jsonPath("$.data.craftsmanshipFeature").exists());
    }

    @Test
    @DisplayName("GET /api/v1/cms/journal - Should list journal articles")
    void testGetJournalArticles() throws Exception {
        mockMvc.perform(get("/api/v1/cms/journal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].slug").value("travel-in-the-nordics"));
    }

    @Test
    @DisplayName("GET /api/v1/cms/journal/{slug} - Should return single article by slug")
    void testGetJournalArticleBySlug() throws Exception {
        mockMvc.perform(get("/api/v1/cms/journal/travel-in-the-nordics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Travel in the Nordics: Minimalist Guide"))
                .andExpect(jsonPath("$.data.author").value("Elena Rostova"));
    }

    @Test
    @DisplayName("GET /api/v1/cms/faqs - Should return categorized active FAQs")
    void testGetFaqs() throws Exception {
        mockMvc.perform(get("/api/v1/cms/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].category").value("Materials & Craft"))
                .andExpect(jsonPath("$.data[0].items[0].question").value("What leather grade do you use?"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("PUT /api/v1/admin/cms/content - Admin should update homepage content")
    void testAdminUpdateHomepageContent() throws Exception {
        CMSContentDto updateDto = CMSContentDto.builder()
                .announcementBar(AnnouncementBarDto.builder()
                        .enabled(true)
                        .text("Special Autumn Capsule Collection Now Live")
                        .link("/categories/new")
                        .build())
                .heroBanner(HeroBannerDto.builder()
                        .eyebrow("Autumn 2026")
                        .title("The Architectural Silhouette")
                        .subtitle("Reduced geometry crafted in full-grain leather.")
                        .build())
                .build();

        mockMvc.perform(put("/api/v1/admin/cms/content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.heroBanner.title").value("The Architectural Silhouette"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/cms/journal - Admin should publish new article")
    void testAdminCreateJournalArticle() throws Exception {
        CreateJournalArticleRequest req = CreateJournalArticleRequest.builder()
                .title("Anatomy of an Everyday Carry")
                .category("Product Stories")
                .readTime("5 min read")
                .author("Marcus Vance")
                .image("https://images.unsplash.com/photo-1548036328-c9fa89d128fa")
                .excerpt("Deconstructing internal volume.")
                .content("Every compartment serves an ergonomic purpose.")
                .tags(List.of("Everyday Carry", "EDC", "Leather"))
                .build();

        mockMvc.perform(post("/api/v1/admin/cms/journal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("anatomy-of-an-everyday-carry"))
                .andExpect(jsonPath("$.data.title").value("Anatomy of an Everyday Carry"));
    }

    @Test
    @WithMockUser(username = "admin@rora-luxury.com", roles = {"ADMIN"})
    @DisplayName("POST /api/v1/admin/cms/faqs - Admin should create new FAQ item")
    void testAdminCreateFaq() throws Exception {
        CreateFaqItemRequest req = CreateFaqItemRequest.builder()
                .category("Shipping & Delivery")
                .question("Do you offer same-day delivery in Mumbai?")
                .answer("Yes, concierge courier service is available across South Mumbai.")
                .displayOrder(1)
                .isActive(true)
                .build();

        mockMvc.perform(post("/api/v1/admin/cms/faqs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.question").value("Do you offer same-day delivery in Mumbai?"));
    }

    @Test
    @DisplayName("POST /api/v1/admin/cms/journal - Unauthorized user should be rejected (401)")
    void testAdminUnauthorizedAccess() throws Exception {
        CreateJournalArticleRequest req = CreateJournalArticleRequest.builder()
                .title("Unauthorized Post")
                .category("Design")
                .readTime("2 min")
                .image("https://example.com/img.jpg")
                .excerpt("Test")
                .content("Content")
                .build();

        mockMvc.perform(post("/api/v1/admin/cms/journal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
