package classfit.example.classfit.calendar.category.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.calendar.category.controller.docs.CategoryControllerDocs;
import classfit.example.classfit.calendar.category.dto.request.CategoryCreateRequest;
import classfit.example.classfit.calendar.category.dto.request.CategoryUpdateRequest;
import classfit.example.classfit.calendar.category.dto.response.CategoryCreateResponse;
import classfit.example.classfit.calendar.category.dto.response.CategoryListResponse;
import classfit.example.classfit.calendar.category.dto.response.CategoryResponse;
import classfit.example.classfit.calendar.category.service.CategoryService;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService categoryService;

    @Override
    @PostMapping("/category")
    public CustomApiResponse<CategoryCreateResponse> addCalendarCategory(
            @AuthMember Member member,
            @RequestBody CategoryCreateRequest request
    ) {
        CategoryCreateResponse result = categoryService.addCategory(member, request);
        return CustomApiResponse.success(result, 201, "캘린더 카테고리 생성 성공");
    }

    @Override
    @GetMapping("/category-list")
    public CustomApiResponse<CategoryListResponse> getCalendarCategories(@AuthMember Member member) {
        CategoryListResponse result = categoryService.getCategories(member);
        return CustomApiResponse.success(result, 200, "캘린더 카테고리 조회 성공");
    }

    @Override
    @PatchMapping("/{categoryId}")
    public CustomApiResponse<CategoryResponse> updateCalendarCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryUpdateRequest request
    ) {
        CategoryResponse result = categoryService.updateCategory(categoryId, request);
        return CustomApiResponse.success(result, 200, "캘린더 카테고리 수정 성공");
    }

    @Override
    @DeleteMapping("/{categoryId}")
    public CustomApiResponse<CategoryResponse> deleteCalendarCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return CustomApiResponse.success(null, 204, "캘린더 카테고리 삭제 성공");
    }
}
