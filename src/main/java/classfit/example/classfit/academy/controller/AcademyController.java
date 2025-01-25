package classfit.example.classfit.academy.controller;

import classfit.example.classfit.academy.controller.docs.AcademyControllerDocs;
import classfit.example.classfit.academy.dto.request.AcademyCreateRequest;
import classfit.example.classfit.academy.dto.request.AcademyJoinRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.academy.service.AcademyService;
import classfit.example.classfit.common.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/academy")
public class AcademyController implements AcademyControllerDocs {

    private final AcademyService academyService;

    @Override
    @PostMapping("/create")
    public CustomApiResponse<AcademyResponse> createAcademy(AcademyCreateRequest request) {
        AcademyResponse academyResponse = academyService.createAcademy(request);
        return CustomApiResponse.success(academyResponse, 200, "학원 생성 성공");
    }

    @Override
    @PostMapping("/invite")
    public CustomApiResponse<Void> joinAcademy(AcademyJoinRequest request) {
        academyService.joinAcademy(request);
        return CustomApiResponse.success(null, 200, "학원 가입 성공");
    }
}
