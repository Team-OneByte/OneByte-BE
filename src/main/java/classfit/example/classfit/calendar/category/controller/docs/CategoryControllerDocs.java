package classfit.example.classfit.calendar.category.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.calendar.category.dto.request.CategoryCreateRequest;
import classfit.example.classfit.calendar.category.dto.request.CategoryUpdateRequest;
import classfit.example.classfit.calendar.category.dto.response.CategoryCreateResponse;
import classfit.example.classfit.calendar.category.dto.response.CategoryListResponse;
import classfit.example.classfit.calendar.category.dto.response.CategoryResponse;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "캘린더 카테고리 컨트롤러", description = "캘린더 카테고리 관련 API입니다.")
public interface CategoryControllerDocs {

    @Operation(summary = "캘린더 카테고리 추가", description = "캘린더 카테고리를 추가하는 API입니다.", responses = {
            @ApiResponse(responseCode = "201", description = "캘린더 카테고리 생성 성공")
    })
    CustomApiResponse<CategoryCreateResponse> addCalendarCategory(
            @AuthMember Member member,
            @RequestBody CategoryCreateRequest request
    );

    @Operation(summary = "캘린더 카테고리 조회", description = "캘린더 카테고리를 조회하는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "캘린더 카테고리 조회 성공")
    })
    CustomApiResponse<CategoryListResponse> getCalendarCategories(@AuthMember Member member);

    @Operation(summary = "캘린더 카테고리 수정", description = "캘린더 카테고리를 수정하는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "캘린더 카테고리 수정 성공")
    })
    CustomApiResponse<CategoryResponse> updateCalendarCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryUpdateRequest request
    );

    @Operation(summary = "캘린더 카테고리 삭제", description = "캘린더 카테고리를 삭제하는 API입니다.", responses = {
            @ApiResponse(responseCode = "204", description = "캘린더 카테고리 삭제 성공")
    })
    CustomApiResponse<CategoryResponse> deleteCalendarCategory(@PathVariable Long categoryId);
}
