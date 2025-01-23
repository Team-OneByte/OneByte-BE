package classfit.example.classfit.calendarCategory.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.calendarCategory.dto.request.CalendarCategoryCreateRequest;
import classfit.example.classfit.calendarCategory.dto.request.CalendarCategoryUpdateRequest;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryCreateResponse;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryListResponse;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryResponse;
import classfit.example.classfit.calendarCategory.service.CalendarCategoryService;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
@Tag(name = "일정관리 카테고리 컨트롤러", description = "일정관리 카테고리 관련 API입니다.")
public class CalendarCategoryController {
    private final CalendarCategoryService calendarCategoryService;

    @PostMapping("/category")
    @Operation(summary = "캘린더 카테고리 추가", description = "캘린더 카테고리 추가하는 api 입니다.")
    public CustomApiResponse<CalendarCategoryCreateResponse> addCalendarCategory(
        @AuthMember Member member,
        @RequestBody CalendarCategoryCreateRequest request
    ) {
        CalendarCategoryCreateResponse result = calendarCategoryService.addCategory(member, request);
        return CustomApiResponse.success(result, 201, "CREATED");
    }

    @GetMapping("/category-list")
    @Operation(summary = "캘린더 카테고리 조회", description = "캘린더 카테고리 조회하는 api 입니다.")
    public CustomApiResponse<CalendarCategoryListResponse> getCalendarCategories(@AuthMember Member member) {
        CalendarCategoryListResponse result = calendarCategoryService.getCategories(member);
        return CustomApiResponse.success(result, 200, "SUCCESS");
    }

    @PatchMapping("/{categoryId}")
    @Operation(summary = "캘린더 카테고리 수정", description = "캘린더 카테고리 수정하는 api 입니다.")
    public CustomApiResponse<CalendarCategoryResponse> updateCalendarCategory(
        @PathVariable Long categoryId,
        @RequestBody CalendarCategoryUpdateRequest request
    ) {
        CalendarCategoryResponse result = calendarCategoryService.updateCategory(categoryId, request);
        return CustomApiResponse.success(result, 200, "UPDATED");
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "캘린더 카테고리 삭제", description = "캘린더 카테고리 삭제하는 api 입니다.")
    public CustomApiResponse<CalendarCategoryResponse> updateCalendarCategories(
        @PathVariable Long categoryId
    ) {
        calendarCategoryService.deleteCategory(categoryId);
        return CustomApiResponse.success(null, 204, "DELETED");
    }
}
