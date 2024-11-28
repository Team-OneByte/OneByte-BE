package classfit.example.classfit.academy.controller;

import classfit.example.classfit.academy.service.AcademyService;
import classfit.example.classfit.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/academy")
public class AcademyController {

    private final AcademyService academyService;

    @GetMapping("/code")
    public ApiResponse<String> createCode() {
        String code = academyService.createCode();
        return ApiResponse.success(code, 200, "학원 코드가 생성되었습니다.");
    }

}
