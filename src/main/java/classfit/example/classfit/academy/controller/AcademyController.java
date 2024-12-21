package classfit.example.classfit.academy.controller;

import classfit.example.classfit.academy.dto.request.AcademyCreateRequest;
import classfit.example.classfit.academy.dto.request.AcademyJoinRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.academy.service.AcademyService;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/academy")
@Tag(name = "학원 컨트롤러", description = "학원 관련 API입니다.")
public class AcademyController {

    private final AcademyService academyService;

    @PostMapping("/create")
    @Operation(summary = "학원 생성", description = "학원 생성 API 입니다.")
    public ApiResponse<AcademyResponse> createAcademy(@RequestBody AcademyCreateRequest request) {
        AcademyResponse academyResponse = academyService.createAcademy(request);
        return ApiResponse.success(academyResponse, 200, "학원 생성이 완료되었습니다");
    }

    @GetMapping("/code")
    @Operation(summary = "학원 코드 생성", description = "학원 코드 생성 API 입니다.")
    public ApiResponse<String> createCode() {
        String code = academyService.createCode();
        return ApiResponse.success(code, 200, "학원 코드가 생성되었습니다.");
    }

    @PostMapping("/invite")
    @Operation(summary = "학원 가입", description = "학원 코드로 등록된 학원에 가입하는 API 입니다.")
    public ApiResponse<Nullable> joinAcademy(@AuthMember Member member, @RequestBody AcademyJoinRequest request) {
        academyService.joinAcademy(member, request);
        return ApiResponse.success(null, 200, "학원 가입을 성공했습니다.");
    }
}
