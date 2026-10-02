package com.rora.backend.cms.controller;

import com.rora.backend.cms.dto.CMSContentDto;
import com.rora.backend.cms.dto.FaqCategoryDto;
import com.rora.backend.cms.dto.JournalArticleDto;
import com.rora.backend.cms.service.CmsService;
import com.rora.backend.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cms")
@RequiredArgsConstructor
@Tag(name = "CMS & Content", description = "Public endpoints for homepage editorial content, journal articles, and store FAQs")
public class CmsController {

    private final CmsService cmsService;

    @GetMapping("/content")
    @Operation(summary = "Get Homepage CMS Content", description = "Retrieve announcement bar, hero banner, and craftsmanship features")
    public ResponseEntity<ApiResponse<CMSContentDto>> getHomepageCms() {
        CMSContentDto content = cmsService.getHomepageCms();
        return ResponseEntity.ok(ApiResponse.success("Homepage content retrieved successfully", content));
    }

    @GetMapping("/journal")
    @Operation(summary = "List Journal Articles", description = "Retrieve published journal articles optionally filtered by category")
    public ResponseEntity<ApiResponse<List<JournalArticleDto>>> getJournalArticles(
            @RequestParam(required = false) String category) {
        List<JournalArticleDto> articles = cmsService.getJournalArticles(category);
        return ResponseEntity.ok(ApiResponse.success("Journal articles retrieved successfully", articles));
    }

    @GetMapping("/journal/{slugOrId}")
    @Operation(summary = "Get Single Journal Article", description = "Retrieve full editorial story by slug or unique ID")
    public ResponseEntity<ApiResponse<JournalArticleDto>> getJournalArticleBySlugOrId(
            @PathVariable String slugOrId) {
        JournalArticleDto article = cmsService.getJournalArticleBySlugOrId(slugOrId);
        return ResponseEntity.ok(ApiResponse.success("Journal article retrieved successfully", article));
    }

    @GetMapping("/faqs")
    @Operation(summary = "List Store FAQs", description = "Retrieve active FAQ items categorized for help center and product support")
    public ResponseEntity<ApiResponse<List<FaqCategoryDto>>> getFaqs() {
        List<FaqCategoryDto> faqs = cmsService.getGroupedFaqs();
        return ResponseEntity.ok(ApiResponse.success("FAQ categories retrieved successfully", faqs));
    }
}
