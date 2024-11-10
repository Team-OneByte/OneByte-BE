package classfit.example.classfit.category.controller;

import classfit.example.classfit.category.dto.request.MainClassRequest;
import classfit.example.classfit.category.dto.response.MainClassResponse;
import classfit.example.classfit.category.service.MainClassService;
import classfit.example.classfit.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class MainClassController {
    private final MainClassService mainClassService;

    @PostMapping("/main-category")
    public ApiResponse<MainClassResponse> addMainClass(
            @RequestHeader("member-no") Long memberId,
            @RequestBody MainClassRequest req
    ) {
        return mainClassService.addMainClass(memberId,req);
    }
}
