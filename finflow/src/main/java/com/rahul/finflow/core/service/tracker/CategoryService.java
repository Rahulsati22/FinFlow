package com.rahul.finflow.core.service.tracker;


import com.rahul.finflow.api.dto.tracker.CategoryRequest;
import com.rahul.finflow.api.dto.tracker.CategoryResponse;
import com.rahul.finflow.infrastructure.persistence.entity.UserEntity;
import com.rahul.finflow.infrastructure.persistence.entity.tracker.CategoryEntity;
import com.rahul.finflow.infrastructure.persistence.repository.UserRepository;
import com.rahul.finflow.infrastructure.persistence.repository.tracker.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getUserCategories(String email){
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        List<CategoryEntity> categoryResponses = categoryRepository.findAllByUserIdIncludingDefaults(userEntity.getId());
        return categoryResponses.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest, String email){
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        CategoryEntity categoryEntity = CategoryEntity.
                builder().
                name(categoryRequest.name()).
                icon(categoryRequest.icon()).
                user(userEntity).
                build();

        CategoryEntity categoryEntitySaved = categoryRepository.save(categoryEntity);
        return mapToResponse(categoryEntitySaved);
    }

    private CategoryResponse mapToResponse(CategoryEntity categoryEntity){
        return new CategoryResponse(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getIcon(),
                categoryEntity.getUser() == null
        );
    }

}
