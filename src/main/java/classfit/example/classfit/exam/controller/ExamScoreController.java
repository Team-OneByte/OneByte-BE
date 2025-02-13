package classfit.example.classfit.exam.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.exam.controller.docs.ExamScoreControllerDocs;
import classfit.example.classfit.exam.dto.examscore.request.CreateExamScoreRequest;
import classfit.example.classfit.exam.dto.examscore.request.UpdateExamScoreRequest;
import classfit.example.classfit.exam.dto.examscore.response.CreateExamScoreResponse;
import classfit.example.classfit.exam.dto.examscore.response.UpdateExamScoreResponse;
import classfit.example.classfit.exam.service.ExamScoreService;
import classfit.example.classfit.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exam-score")
@RequiredArgsConstructor
public class ExamScoreController implements ExamScoreControllerDocs {

    private final ExamScoreService examScoreService;

    @Override
    @PostMapping("/")
    public CustomApiResponse<List<CreateExamScoreResponse>> createExamScore(
            @AuthMember Member findMember,
            @RequestBody List<CreateExamScoreRequest> req
    ) {
        List<CreateExamScoreResponse> response = examScoreService.createExamScore(findMember, req);
        return CustomApiResponse.success(response, 201, "학생 시험 성적 등록 성공");
    }


    @Override
    @PatchMapping("/update/{examId}")
    public CustomApiResponse<UpdateExamScoreResponse> updateStudentScore(
            @AuthMember Member findMember,
            @PathVariable(name = "examId") Long examId,
            @RequestBody List<UpdateExamScoreRequest> requests
    ) {
        UpdateExamScoreResponse response = examScoreService.updateExamScore(findMember, examId,
                requests);
        return CustomApiResponse.success(response, 200, "학생 시험 점수 수정 성공");
    }

}
