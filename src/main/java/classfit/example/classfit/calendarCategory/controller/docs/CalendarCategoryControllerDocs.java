package classfit.example.classfit.calendarCategory.controller.docs;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.calendarCategory.dto.request.CalendarCategoryCreateRequest;
import classfit.example.classfit.calendarCategory.dto.request.CalendarCategoryUpdateRequest;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryCreateResponse;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryListResponse;
import classfit.example.classfit.calendarCategory.dto.response.CalendarCategoryResponse;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "캘린더 카테고리 컨트롤러", description = "캘린더 카테고리 관련 API입니다.")
public interface CalendarCategoryControllerDocs {

    @Operation(summary = "캘린더 카테고리 추가", description = "캘린더 카테고리를 추가하는 API입니다.", responses = {
        @ApiResponse(responseCode = "201", description = "캘린더 카테고리 생성 성공")
    })
    CustomApiResponse<CalendarCategoryCreateResponse> addCalendarCategory(
        @AuthMember Member member,
        @RequestBody CalendarCategoryCreateRequest request
    );

    @Operation(summary = "캘린더 카테고리 조회", description = "캘린더 카테고리를 조회하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "캘린더 카테고리 조회 성공")
    })
    CustomApiResponse<CalendarCategoryListResponse> getCalendarCategories(@AuthMember Member member);

    @Operation(summary = "캘린더 카테고리 수정", description = "캘린더 카테고리를 수정하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "캘린더 카테고리 수정 성공")
    })
    CustomApiResponse<CalendarCategoryResponse> updateCalendarCategory(
        @PathVariable Long categoryId,
        @RequestBody CalendarCategoryUpdateRequest request
    );

    @Operation(summary = "캘린더 카테고리 삭제", description = "캘린더 카테고리를 삭제하는 API입니다.", responses = {
        @ApiResponse(responseCode = "204", description = "캘린더 카테고리 삭제 성공")
    })
    CustomApiResponse<CalendarCategoryResponse> deleteCalendarCategory(@PathVariable Long categoryId);
}
