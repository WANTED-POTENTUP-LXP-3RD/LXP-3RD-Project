package com.lxp.aplus.category.presentation.controller;

import com.lxp.aplus.category.application.result.CategoryResult;
import com.lxp.aplus.category.application.usecase.CategoryQueryUseCase;
import com.lxp.aplus.category.presentation.response.CategoryResponse;
import com.lxp.aplus.common.result.ResultResponse;
import com.lxp.aplus.common.result.code.CategoryResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryQueryUseCase categoryQueryUseCase;

    @GetMapping
    public ResponseEntity<ResultResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResult> results = categoryQueryUseCase.getAllCategories();

        return ResponseEntity
                .status(CategoryResultCode.CATEGORY_LIST_SUCCESS.getStatus())
                .body(ResultResponse.of(CategoryResultCode.CATEGORY_LIST_SUCCESS, CategoryResponse.from(results)));
    }
}
