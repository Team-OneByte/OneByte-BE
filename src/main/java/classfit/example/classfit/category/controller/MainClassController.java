package classfit.example.classfit.category.controller;

import classfit.example.classfit.category.dto.request.MainClassRequest;
import classfit.example.classfit.category.dto.response.MainClassResponse;
import classfit.example.classfit.category.service.MainClassService;
import classfit.example.classfit.common.ApiResponse;
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
@Tag(name = "메인 클래스 컨트롤러", description = "메인 클래스 관련 API입니다.")
public class MainClassController {

    private final MainClassService mainClassService;

    @PostMapping("/main-category")
    @Operation(summary = "메인 클래스 추가", description = "메인 클래스 추가하는 api 입니다.")
    public ApiResponse<MainClassResponse> addMainClass(
            @RequestHeader("member-no") Long memberId,
            @RequestBody MainClassRequest req
    ) {
        MainClassResponse result = mainClassService.addMainClass(memberId, req);
        return ApiResponse.success(result, 201, "CREATED");
    }

    @PatchMapping("/main-category/{mainClassId}")
    @Operation(summary = "메인 클래스 수정", description = "메인 클래스의 이름을 수정하는 api 입니다.")
    public ApiResponse<MainClassResponse> updateMainClass(@RequestHeader("member-no") Long memberId,
            @PathVariable(name = "mainClassId") Long mainClassId,
            @RequestBody MainClassRequest req) {
        MainClassResponse result = mainClassService.updateMainClass(memberId, mainClassId, req);
        return ApiResponse.success(result, 200, "UPDATED");
    }

    @DeleteMapping("/main-category/{mainClassId}")
    @Operation(summary = "메인 클래스 삭제", description = "메인 클래스 삭제하는 api 입니다.")
    public ApiResponse<?> deleteMainClass(@RequestHeader("member-no") Long memberId,
            @PathVariable(name = "mainClassId") Long mainClassId) {
        mainClassService.deleteMainClass(memberId, mainClassId);
        return ApiResponse.success(null, 200, "DELETED");
    }
}
