package classfit.example.classfit.category.controller;

import classfit.example.classfit.category.dto.response.ClassInfoResponse;
import classfit.example.classfit.category.service.ClassInfoService;
import classfit.example.classfit.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class ClassInfoController {
    private final ClassInfoService classInfoService;

    @GetMapping("/class-info")
    public ApiResponse<List<ClassInfoResponse>> getClassInfo() {
        List<ClassInfoResponse> categories = classInfoService.getClasses();
        return ApiResponse.success(categories, 200, "SUCCESS");
    }
}
