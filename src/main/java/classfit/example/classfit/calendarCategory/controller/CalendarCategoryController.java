package classfit.example.classfit.calendarCategory.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.calendarCategory.controller.docs.CalendarCategoryControllerDocs;
import classfit.example.classfit.calendarCategory.dto.request.CalendarCategoryCreateRequest;
import classfit.example.classfit.calendarCategory.dto.request.CalendarCategoryUpdateRequest;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryCreateResponse;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryListResponse;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryResponse;
import classfit.example.classfit.calendarCategory.service.CalendarCategoryService;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarCategoryController implements CalendarCategoryControllerDocs {

    private final CalendarCategoryService calendarCategoryService;

    @Override
    @PostMapping("/category")
    public CustomApiResponse<CalendarCategoryCreateResponse> addCalendarCategory(
        @AuthMember Member member,
        @RequestBody CalendarCategoryCreateRequest request
    ) {
        CalendarCategoryCreateResponse result = calendarCategoryService.addCategory(member, request);
        return CustomApiResponse.success(result, 201, "캘린더 카테고리가 생성되었습니다.");
    }

    @Override
    @GetMapping("/category-list")
    public CustomApiResponse<CalendarCategoryListResponse> getCalendarCategories(@AuthMember Member member) {
        CalendarCategoryListResponse result = calendarCategoryService.getCategories(member);
        return CustomApiResponse.success(result, 200, "캘린더 카테고리를 성공적으로 조회했습니다.");
    }

    @Override
    @PatchMapping("/{categoryId}")
    public CustomApiResponse<CalendarCategoryResponse> updateCalendarCategory(
        @PathVariable Long categoryId,
        @RequestBody CalendarCategoryUpdateRequest request
    ) {
        CalendarCategoryResponse result = calendarCategoryService.updateCategory(categoryId, request);
        return CustomApiResponse.success(result, 200, "캘린더 카테고리가 수정되었습니다.");
    }

    @Override
    @DeleteMapping("/{categoryId}")
    public CustomApiResponse<CalendarCategoryResponse> deleteCalendarCategory(@PathVariable Long categoryId) {
        calendarCategoryService.deleteCategory(categoryId);
        return CustomApiResponse.success(null, 204, "캘린더 카테고리가 삭제되었습니다.");
    }
}
