package classfit.example.classfit.category.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.category.controller.docs.MainClassControllerDocs;
import classfit.example.classfit.category.dto.request.MainClassRequest;
import classfit.example.classfit.category.dto.response.AllMainClassResponse;
import classfit.example.classfit.category.dto.response.MainClassResponse;
import classfit.example.classfit.category.service.MainClassService;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class MainClassController implements MainClassControllerDocs {

    private final MainClassService mainClassService;

    @Override
    @PostMapping("/main-category")
    public CustomApiResponse<MainClassResponse> createMainClass(
            @AuthMember Member findMember,
            @Valid @RequestBody MainClassRequest request
    ) {
        MainClassResponse result = mainClassService.createMainClass(findMember, request);
        return CustomApiResponse.success(result, 201, "메인 클래스 생성 성공");
    }

    @Override
    @GetMapping("/main-category")
    public CustomApiResponse<List<AllMainClassResponse>> showMainClass(@AuthMember Member findMember) {
        List<AllMainClassResponse> result = mainClassService.showMainClass(findMember);
        return CustomApiResponse.success(result, 200, "메인 클래스 조회 성공");
    }

    @Override
    @DeleteMapping("/main-category/{mainClassId}")
    public CustomApiResponse<Void> deleteMainClass(
            @AuthMember Member findMember,
            @PathVariable(name = "mainClassId") Long mainClassId
    ) {
        mainClassService.deleteMainClass(findMember, mainClassId);
        return CustomApiResponse.success(null, 200, "메인 클래스 삭제 성공");
    }

    @Override
    @PatchMapping("/main-category/{mainClassId}/update")
    public CustomApiResponse<MainClassResponse> updateMainClass(
            @AuthMember Member findMember,
            @PathVariable(name = "mainClassId") Long mainClassId,
            @Valid @RequestBody MainClassRequest request
    ) {
        MainClassResponse updateMainClass = mainClassService.updateMainClass(findMember, mainClassId, request);
        return CustomApiResponse.success(updateMainClass, 200, "메인 클래스 수정 성공");
    }

}
