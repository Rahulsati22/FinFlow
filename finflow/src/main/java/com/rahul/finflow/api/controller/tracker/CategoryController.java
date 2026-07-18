package com.rahul.finflow.api.controller.tracker;

import com.rahul.finflow.api.dto.tracker.CategoryRequest;
import com.rahul.finflow.api.dto.tracker.CategoryResponse;
// import com.rahul.finflow.core.service.tracker.CategoryService; // We will create this next!
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


    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getUserCategories(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
         return null; // Placeholder until service is built
    }


    //particular user is requesting to create this category->not system default
    @PostMapping
    public ResponseEntity<CategoryResponse> createCustomCategory(
            @Valid @RequestBody CategoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return null; 
    }
}