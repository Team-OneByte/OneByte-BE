package classfit.example.classfit.studentExam.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.studentExam.controller.docs.ExamControllerDocs;
import classfit.example.classfit.studentExam.dto.examScoreRequest.CreateExamScoreRequest;
import classfit.example.classfit.studentExam.dto.examScoreResponse.CreateExamScoreResponse;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import classfit.example.classfit.studentExam.dto.examRequest.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.examRequest.FindExamRequest;
import classfit.example.classfit.studentExam.dto.examRequest.UpdateExamRequest;
import classfit.example.classfit.studentExam.dto.examScoreRequest.UpdateExamScoreRequest;
import classfit.example.classfit.studentExam.dto.examResponse.*;
import classfit.example.classfit.studentExam.dto.examScoreResponse.UpdateExamScoreResponse;
import classfit.example.classfit.studentExam.service.ExamScoreService;
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
    private final ExamScoreService examScoreService;

    @Override
    @PostMapping
    public CustomApiResponse<CreateExamResponse> createExam(
        @AuthMember Member findMember,
        @RequestBody CreateExamRequest req
    ) {
        CreateExamResponse response = examService.createExam(findMember, req);
        return CustomApiResponse.success(response, 201, "시험 정보 등록 성공");
    }
    @Override
    @PostMapping("/score")
    public CustomApiResponse<List<CreateExamScoreResponse>> createExamScore(
            @AuthMember Member findMember,
            @RequestBody List<CreateExamScoreRequest> req
    ) {
        List<CreateExamScoreResponse> response = examScoreService.createExamScore(findMember,req);
        return CustomApiResponse.success(response,201,"학생 시험 성적 등록 성공");
    }

    @Override
    @GetMapping("/{examId}")
    public CustomApiResponse<List<FindExamStudentResponse>> findExamClassStudent(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId
    ) {
        List<FindExamStudentResponse> response = examService.findExamClassStudent(findMember,
            examId);
        return CustomApiResponse.success(response, 200, "해당 클래스 학생 조회 성공");
    }

    @Override
    @PostMapping("/find-exam")
    public CustomApiResponse<List<FindExamResponse>> findExamList(
        @AuthMember Member findMember,
        @RequestBody FindExamRequest request
    ) {
        List<FindExamResponse> response = examService.findExamList(findMember, request);
        return CustomApiResponse.success(response, 200, "시험 리스트 조회 성공");
    }

    @Override
    @GetMapping("/find-exam/{examId}")
    public CustomApiResponse<ShowExamDetailResponse> showExamDetail(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId
    ) {
        ShowExamDetailResponse response = examService.showExamDetail(findMember, examId);
        return CustomApiResponse.success(response, 200, "시험 상세 조회 성공");
    }

    @Override
    @PutMapping("/{examId}")
    public CustomApiResponse<UpdateExamResponse> updateExam(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId,
        @RequestBody UpdateExamRequest request
    ) {
        UpdateExamResponse response = examService.updateExam(findMember, examId, request);
        return CustomApiResponse.success(response, 200, "시험 수정 성공");
    }

    @Override
    @DeleteMapping("/{examId}")
    public ResponseEntity<CustomApiResponse> deleteExam(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId
    ) {
        examService.deleteExam(findMember, examId);
        return ResponseEntity.ok(CustomApiResponse.success(null, 200, "시험 삭제 성공"));
    }

    @PatchMapping("/find-exam/{examId}/score")
    public CustomApiResponse<UpdateExamScoreResponse> updateStudentScore(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId,
        @RequestBody List<UpdateExamScoreRequest> requests
    ) {
        UpdateExamScoreResponse response = examScoreService.updateExamScore(findMember, examId, requests);
        return CustomApiResponse.success(response, 200, "학생 시험 점수 수정 성공");
    }
}
