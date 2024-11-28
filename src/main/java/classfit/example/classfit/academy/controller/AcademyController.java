package classfit.example.classfit.academy.controller;

import classfit.example.classfit.academy.dto.request.AcademyRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.academy.service.AcademyService;
import classfit.example.classfit.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/academy")
@Tag(name = "학원 컨트롤러", description = "학원 관련 API입니다.")
public class AcademyController {

    private final AcademyService academyService;

    @PostMapping("/register")
    @Operation(summary = "학원 등록", description = "학원 등록 API 입니다.")
    public ApiResponse<AcademyResponse> register(@RequestBody AcademyRequest request) {
        AcademyResponse registeredAcademy = academyService.register(request);
        return ApiResponse.success(registeredAcademy, 200, "학원 등록이 완료되었습니다");
    }

    @GetMapping("/code")
    @Operation(summary = "학원 코드 생성", description = "학원 코드 생성 API 입니다.")
    public ApiResponse<String> createCode() {
        String code = academyService.createCode();
        return ApiResponse.success(code, 200, "학원 코드가 생성되었습니다.");
    }
}
