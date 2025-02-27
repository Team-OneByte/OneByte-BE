package classfit.example.classfit.course.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.course.controller.docs.ClassInfoControllerDocs;
import classfit.example.classfit.course.dto.response.ClassInfoResponse;
import classfit.example.classfit.course.service.ClassInfoService;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class ClassInfoController implements ClassInfoControllerDocs {

    private final ClassInfoService classInfoService;

    @Override
    @GetMapping("/class-info")
    public CustomApiResponse<List<ClassInfoResponse>> getClassInfo(@AuthMember Member member) {
        List<ClassInfoResponse> categories = classInfoService.getClasses(member);
        return CustomApiResponse.success(categories, 200, "클래스 정보 조회 성공");
    }
}
