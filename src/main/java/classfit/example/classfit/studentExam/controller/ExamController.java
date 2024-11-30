package classfit.example.classfit.studentExam.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.studentExam.dto.request.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.response.CreateExamResponse;
import classfit.example.classfit.studentExam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ApiResponse<CreateExamResponse> createExam(
            @RequestHeader(name = "member-no",required = false) Long memberId,
            @RequestBody CreateExamRequest req
    ) {
        CreateExamResponse response = examService.createExam(memberId,req);
        return ApiResponse.success(response,201, "CREATED EXAM");
    }
}
