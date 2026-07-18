package com.rahul.finflow.api.controller.tracker;

import com.rahul.finflow.api.dto.tracker.CategoryRequest;
import com.rahul.finflow.api.dto.tracker.CategoryResponse;
import com.rahul.finflow.core.service.tracker.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getUserCategories(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<CategoryResponse> categories = categoryService.getUserCategories(userDetails.getUsername());
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCustomCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CategoryResponse response = categoryService.createCategory(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}