package com.rora.backend.cms.controller;

import com.rora.backend.cms.dto.*;
import com.rora.backend.cms.service.CmsService;
import com.rora.backend.common.ApiResponse;
import com.rora.backend.common.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/cms")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or hasAuthority('CMS_MANAGE')")
@Tag(name = "Admin CMS & Content", description = "Backoffice management of hero banners, editorial journal articles, and store FAQs")
public class AdminCmsController {

    private final CmsService cmsService;

    // --- Homepage CMS Content ---

    @GetMapping("/content")
    @Operation(summary = "Get Full CMS Content", description = "Retrieve current homepage CMS configuration for editing")
    public ResponseEntity<ApiResponse<CMSContentDto>> getAdminCmsContent() {
        CMSContentDto content = cmsService.getHomepageCms();
        return ResponseEntity.ok(ApiResponse.success("CMS content retrieved", content));
    }

    @PutMapping("/content")
    @Operation(summary = "Update Homepage CMS Content", description = "Update hero banners, announcement bar, and craftsmanship stories")
    public ResponseEntity<ApiResponse<CMSContentDto>> updateHomepageCms(
            @Valid @RequestBody CMSContentDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        CMSContentDto updated = cmsService.updateHomepageCms(request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Homepage CMS content updated successfully", updated));
    }

    // --- Journal Management ---

    @GetMapping("/journal")
    @Operation(summary = "Search & List Journal Articles", description = "Paginated article search with category and keyword filters")
    public ResponseEntity<ApiResponse<PagedResponse<JournalArticleDto>>> searchJournalArticles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<JournalArticleDto> response = cmsService.searchJournalArticles(category, query, page, size);
        return ResponseEntity.ok(ApiResponse.success("Journal articles retrieved", response));
    }

    @GetMapping("/journal/{idOrSlug}")
    @Operation(summary = "Get Journal Article by ID", description = "Retrieve single article for backoffice editorial review")
    public ResponseEntity<ApiResponse<JournalArticleDto>> getJournalArticleById(
            @PathVariable String idOrSlug) {
        JournalArticleDto article = cmsService.getJournalArticleBySlugOrId(idOrSlug);
        return ResponseEntity.ok(ApiResponse.success("Journal article retrieved", article));
    }

    @PostMapping("/journal")
    @Operation(summary = "Publish Journal Article", description = "Create and publish a new editorial article")
    public ResponseEntity<ApiResponse<JournalArticleDto>> createJournalArticle(
            @Valid @RequestBody CreateJournalArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        JournalArticleDto created = cmsService.createJournalArticle(request, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Journal article created successfully", created));
    }

    @PutMapping("/journal/{id}")
    @Operation(summary = "Update Journal Article", description = "Modify title, content, imagery, or category of an existing article")
    public ResponseEntity<ApiResponse<JournalArticleDto>> updateJournalArticle(
            @PathVariable String id,
            @Valid @RequestBody UpdateJournalArticleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        JournalArticleDto updated = cmsService.updateJournalArticle(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Journal article updated successfully", updated));
    }

    @DeleteMapping("/journal/{id}")
    @Operation(summary = "Delete Journal Article", description = "Remove an article from the editorial publication")
    public ResponseEntity<ApiResponse<Void>> deleteJournalArticle(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        cmsService.deleteJournalArticle(id, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("Journal article deleted successfully", null));
    }

    // --- FAQ Management ---

    @GetMapping("/faqs")
    @Operation(summary = "List All FAQ Items", description = "Retrieve flat list of all active and inactive FAQ items")
    public ResponseEntity<ApiResponse<List<FaqItemDto>>> getAllFaqItems() {
        List<FaqItemDto> items = cmsService.getAllFaqItems();
        return ResponseEntity.ok(ApiResponse.success("All FAQ items retrieved", items));
    }

    @PostMapping("/faqs")
    @Operation(summary = "Create FAQ Item", description = "Add a new categorized question and answer pair")
    public ResponseEntity<ApiResponse<FaqItemDto>> createFaqItem(
            @Valid @RequestBody CreateFaqItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        FaqItemDto created = cmsService.createFaqItem(request, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("FAQ item created successfully", created));
    }

    @PutMapping("/faqs/{id}")
    @Operation(summary = "Update FAQ Item", description = "Update question, answer, category, or order of an FAQ item")
    public ResponseEntity<ApiResponse<FaqItemDto>> updateFaqItem(
            @PathVariable String id,
            @Valid @RequestBody UpdateFaqItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        FaqItemDto updated = cmsService.updateFaqItem(id, request, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("FAQ item updated successfully", updated));
    }

    @DeleteMapping("/faqs/{id}")
    @Operation(summary = "Delete FAQ Item", description = "Remove an FAQ item from the store knowledge base")
    public ResponseEntity<ApiResponse<Void>> deleteFaqItem(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "system-admin";
        cmsService.deleteFaqItem(id, adminEmail);
        return ResponseEntity.ok(ApiResponse.success("FAQ item deleted successfully", null));
    }
}
