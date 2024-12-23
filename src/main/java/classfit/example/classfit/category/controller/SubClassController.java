package classfit.example.classfit.category.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.dto.request.SubClassRequest;
import classfit.example.classfit.category.dto.response.SubClassResponse;
import classfit.example.classfit.category.service.SubClassService;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "하위 클래스 컨트롤러", description = "하위 클래스 관련 API입니다.")
public class SubClassController {

    private final SubClassService subClassService;

    @PostMapping("/sub-category")
    @Operation(summary = "하위 클래스 추가", description = "하위 클래스 추가하는 api 입니다.")
    public ApiResponse<SubClassResponse> addSubClass(@AuthMember Member findMember,
            @RequestBody SubClassRequest req) {
        SubClassResponse result = subClassService.addSubClass(findMember, req);
        return ApiResponse.success(result, 201, "CREATED");

    }

    @PatchMapping("/sub-category/{subClassId}")
    @Operation(summary = "하위 클래스 수정", description = "하위 클래스 이름을 수정하는 api 입니다.")
    public ApiResponse<SubClassResponse> updateSubClass(
            @AuthMember Member findMember,
            @PathVariable(name = "subClassId") Long subClassId,
            @RequestBody SubClassRequest req) {
        SubClassResponse result = subClassService.updateSubClass(findMember, subClassId, req);
        return ApiResponse.success(result, 200, "UODATED");
    }

    @DeleteMapping("/sub-category/{subClassId}")
    @Operation(summary = "하위 클래스 삭제", description = "하위 클래스 삭제하는 api 입니다.")
    public ApiResponse<?> deleteSubClass(
            @AuthMember Member findMember,
            @PathVariable(name = "subClassId") Long subClassId) {
        subClassService.deleteSubClass(findMember, subClassId);
        return ApiResponse.success(null, 200, "DELETED");
    }

}
