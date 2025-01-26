package classfit.example.classfit.category.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.controller.docs.SubClassControllerDocs;
import classfit.example.classfit.category.dto.request.SubClassRequest;
import classfit.example.classfit.category.dto.response.SubClassResponse;
import classfit.example.classfit.category.service.SubClassService;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class SubClassController implements SubClassControllerDocs {

    private final SubClassService subClassService;

    @Override
    @PostMapping("/sub-category")
    public CustomApiResponse<SubClassResponse> createSubClass(
        @AuthMember Member findMember,
        @RequestBody SubClassRequest req
    ) {
        SubClassResponse result = subClassService.createSubClass(findMember, req);
        return CustomApiResponse.success(result, 201, "하위 클래스 생성 성공");
    }

    @Override
    @PatchMapping("/sub-category/{subClassId}")
    public CustomApiResponse<SubClassResponse> updateSubClass(
        @AuthMember Member findMember,
        @PathVariable(name = "subClassId") Long subClassId,
        @RequestBody SubClassRequest req
    ) {
        SubClassResponse result = subClassService.updateSubClass(findMember, subClassId, req);
        return CustomApiResponse.success(result, 200, "하위 클래스 수정 성공");
    }

    @Override
    @DeleteMapping("/sub-category/{subClassId}")
    public CustomApiResponse<Void> deleteSubClass(
        @AuthMember Member findMember,
        @PathVariable(name = "subClassId") Long subClassId
    ) {
        subClassService.deleteSubClass(findMember, subClassId);
        return CustomApiResponse.success(null, 200, "하위 클래스 삭제 성공");
    }

}
