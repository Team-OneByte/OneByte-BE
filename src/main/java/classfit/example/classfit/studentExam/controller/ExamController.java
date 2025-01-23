package classfit.example.classfit.studentExam.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.studentExam.controller.docs.ExamControllerDocs;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import classfit.example.classfit.studentExam.dto.request.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.request.FindExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateStudentScoreRequest;
import classfit.example.classfit.studentExam.dto.response.*;
import classfit.example.classfit.studentExam.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exam")
@RequiredArgsConstructor
public class ExamController implements ExamControllerDocs {

    private final ExamService examService;

    @Override
    @PostMapping
    public CustomApiResponse<CreateExamResponse> createExam(
        @AuthMember Member findMember,
        @RequestBody CreateExamRequest req
    ) {
        CreateExamResponse response = examService.createExam(findMember, req);
        return CustomApiResponse.success(response, 201, "CREATED EXAM");
    }

    @Override
    @GetMapping("/{examId}")
    public CustomApiResponse<List<ExamClassStudent>> findExamClassStudent(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId
    ) {
        List<ExamClassStudent> response = examService.findExamClassStudent(findMember,
            examId);
        return CustomApiResponse.success(response, 200, "FIND EXAM-STUDENT");
    }

    @Override
    @PostMapping("/findexam")
    public CustomApiResponse<List<FindExamResponse>> findExamList(
        @AuthMember Member findMember,
        @RequestBody FindExamRequest request
    ) {
        List<FindExamResponse> response = examService.findExamList(findMember, request);
        return CustomApiResponse.success(response, 200, "FIND EXAM-LIST");
    }

    @Override
    @GetMapping("/findexam/{examId}")
    public CustomApiResponse<ShowExamDetailResponse> showExamDetail(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId
    ) {
        ShowExamDetailResponse response = examService.showExamDetail(findMember, examId);
        return CustomApiResponse.success(response, 200, "FIND EXAM");
    }

    @Override
    @PutMapping("/{examId}")
    public CustomApiResponse<UpdateExamResponse> updateExam(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId,
        @RequestBody UpdateExamRequest request
    ) {
        UpdateExamResponse response = examService.updateExam(findMember, examId, request);
        return CustomApiResponse.success(response, 200, "UPDATED EXAM");
    }

    @Override
    @DeleteMapping("/{examId}")
    public ResponseEntity<CustomApiResponse> deleteExam(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId
    ) {
        examService.deleteExam(findMember, examId);
        return ResponseEntity.ok(CustomApiResponse.success(null, 200, "DELETED EXAM"));
    }

    @PatchMapping("/findexam/{examId}/score")
    public CustomApiResponse<UpdateStudentScoreResponse> updateStudentScore(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId,
        @RequestBody List<UpdateStudentScoreRequest> requests
    ) {
        UpdateStudentScoreResponse response = examService.updateStudentScore(findMember, examId, requests);
        return CustomApiResponse.success(response, 200, "UPDATED-STUDENT-SCORE");
    }
}
