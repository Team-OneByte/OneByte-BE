package classfit.example.classfit.category.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.dto.response.ClassInfoResponse;
import classfit.example.classfit.category.service.ClassInfoService;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "클래스 정보 조회 컨트롤러", description = "클래스 정보 조회 API입니다.")
public class ClassInfoController {
    private final ClassInfoService classInfoService;

    @GetMapping("/class-info")
    @Operation(summary = "클래스 정보 조회", description = "클래스 정보 조회 api 입니다.")
    public ApiResponse<List<ClassInfoResponse>> getClassInfo(@AuthMember Member member) {
        List<ClassInfoResponse> categories = classInfoService.getClasses(member);
        return ApiResponse.success(categories, 200, "SUCCESS");
    }
}
