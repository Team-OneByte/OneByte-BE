package classfit.example.classfit.category.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.dto.request.MainClassRequest;
import classfit.example.classfit.category.dto.response.AllMainClassResponse;
import classfit.example.classfit.category.dto.response.MainClassResponse;
import classfit.example.classfit.category.service.MainClassService;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "메인 클래스 컨트롤러", description = "메인 클래스 관련 API입니다.")
public class MainClassController {

    private final MainClassService mainClassService;

    @PostMapping("/main-category")
    @Operation(summary = "메인 클래스 추가", description = "메인 클래스 추가하는 api 입니다.")
    public ApiResponse<MainClassResponse> addMainClass(
        @AuthMember Member findMember,
        @Valid @RequestBody MainClassRequest req
    ) {
        MainClassResponse result = mainClassService.addMainClass(findMember, req);
        return ApiResponse.success(result, 201, "CREATED");
    }

    @GetMapping("/main-category")
    @Operation(summary = "추가된 메인 클래스 조회", description = "메인 클래스를 조회할 수 있는 api 입니다.")
    public ApiResponse<List<AllMainClassResponse>> showMainClass(
        @AuthMember Member findMember) {
        List<AllMainClassResponse> result = mainClassService.showMainClass(findMember);
        return ApiResponse.success(result, 200, "SUCCESS");
    }

    @DeleteMapping("/main-category/{mainClassId}")
    @Operation(summary = "메인 클래스 삭제", description = "메인 클래스 삭제하는 api 입니다. 서브클래스 먼저 삭제 후 삭제 가능")
    public ApiResponse<?> deleteMainClass(@AuthMember Member findMember,
                                          @PathVariable(name = "mainClassId") Long mainClassId) {
        mainClassService.deleteMainClass(findMember, mainClassId);
        return ApiResponse.success(null, 200, "DELETED");
    }

    @PatchMapping("/main-category/{mainClassId}/update")
    @Operation(summary = "메인 클래스 수정", description = "메인 클래스 수정하는 api 입니다.")
    public ApiResponse<MainClassResponse> updateMainClass(@AuthMember Member findMember,
                                                          @PathVariable(name = "mainClassId") Long mainClassId,
                                                          @Valid @RequestBody MainClassRequest request) {
        MainClassResponse updateMainClass = mainClassService.updateMainClass(findMember, mainClassId, request);
        return ApiResponse.success(updateMainClass, 200, "DELETED");
    }

}
