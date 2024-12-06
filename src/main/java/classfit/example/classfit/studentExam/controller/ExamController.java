package classfit.example.classfit.studentExam.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.studentExam.dto.request.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.request.FindExamRequest;
import classfit.example.classfit.studentExam.dto.response.CreateExamResponse;
import classfit.example.classfit.studentExam.dto.response.FindExamResponse;
import classfit.example.classfit.studentExam.dto.response.ShowExamClassStudentResponse;
import classfit.example.classfit.studentExam.dto.response.ShowExamDetailResponse;
import classfit.example.classfit.studentExam.service.ExamService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @RequestBody CreateExamRequest req) {
        CreateExamResponse response = examService.createExam(memberId, req);
        return ApiResponse.success(response, 201, "CREATED EXAM");
    }

    @GetMapping("/{examId}")
    public ApiResponse<List<ShowExamClassStudentResponse>> findExamClassStuent(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @PathVariable(name = "examId") Long examId
    ) {
        List<ShowExamClassStudentResponse> response = examService.findExamClassStuent(memberId,
                examId);
        return ApiResponse.success(response, 200, "FIND EXAM-STUDENT");
    }

    @PostMapping("/findexam")
    public ApiResponse<List<FindExamResponse>> findExamList(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @RequestBody FindExamRequest request
    ) {
        List<FindExamResponse> response = examService.findExamList(memberId, request);
        return ApiResponse.success(response, 200, "FIND EXAM-LIST");
    }

    @GetMapping("/findexam/{examId}")
    public ApiResponse<ShowExamDetailResponse> showExamDetail(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @PathVariable(name = "examId") Long examId) {
        ShowExamDetailResponse response = examService.showExamDetail(memberId, examId);

        return ApiResponse.success(response, 200, "FIND EXAM");
    }
}
