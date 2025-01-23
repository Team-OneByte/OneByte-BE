package classfit.example.classfit.studentExam.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import classfit.example.classfit.studentExam.dto.request.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.request.FindExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateStudentScoreRequest;
import classfit.example.classfit.studentExam.dto.response.*;
import classfit.example.classfit.studentExam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exam")
@RequiredArgsConstructor
@Tag(name = "성적관리 API", description = "성적관리 API입니다.")
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @Operation(summary = "시험 정보 등록", description = "시험 정보 등록하는 API 입니다.초기 생성 시 학생 성적 0점으로 생성 학생 성적 수정에서 점수등록 가능 / PF일 경우 highestScore == -1 ,evaluation 경우 highestScore == -2")
    public CustomApiResponse<CreateExamResponse> createExam(
        @AuthMember Member findMember,
        @RequestBody CreateExamRequest req) {
        CreateExamResponse response = examService.createExam(findMember, req);
        return CustomApiResponse.success(response, 201, "CREATED EXAM");
    }

    @GetMapping("/{examId}")
    @Operation(summary = "시험 등록 시 해당 클래스 학생 조회", description = "시험 등록시 해당 클래스 학생 조회하는 API 입니다.")
    public CustomApiResponse<List<ExamClassStudent>> findExamClassStuent(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId
    ) {
        List<ExamClassStudent> response = examService.findExamClassStudent(findMember,
            examId);
        return CustomApiResponse.success(response, 200, "FIND EXAM-STUDENT");
    }

    @PostMapping("/findexam")
    @Operation(summary = "시험 리스트 조회", description = "시험 이름과 작성자로 시험 검색하는 API 입니다. request값이 둘다 null일시 전체조회")
    public CustomApiResponse<List<FindExamResponse>> findExamList(
        @AuthMember Member findMember,
        @RequestBody FindExamRequest request
    ) {
        List<FindExamResponse> response = examService.findExamList(findMember, request);
        return CustomApiResponse.success(response, 200, "FIND EXAM-LIST");
    }

    @GetMapping("/findexam/{examId}")
    @Operation(summary = "시험 상세 조회", description = "시험 상세 내용 조회 API 입니다.")
    public CustomApiResponse<ShowExamDetailResponse> showExamDetail(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId) {
        ShowExamDetailResponse response = examService.showExamDetail(findMember, examId);

        return CustomApiResponse.success(response, 200, "FIND EXAM");
    }

    @PutMapping("/{examId}")
    @Operation(summary = "시험 수정", description = "시험 수정하는 API 입니다.")
    public CustomApiResponse<UpdateExamResponse> updateExam(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId,
        @RequestBody UpdateExamRequest request) {
        UpdateExamResponse response = examService.updateExam(findMember, examId, request);
        return CustomApiResponse.success(response, 200, "UPDATED EXAM");
    }

    @DeleteMapping("/{examId}")
    @Operation(summary = "시험 삭제", description = "시험 삭제하는 API 입니다.")
    public ResponseEntity<CustomApiResponse> deleteExam(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId) {
        examService.deleteExam(findMember, examId);
        return ResponseEntity.ok(CustomApiResponse.success(null, 200, "DELETED EXAM"));
    }

    @PatchMapping("/findexam/{examId}/score")
    @Operation(summary = "학생들 성적 수정", description = "학생들 성적 수정하는 API 입니다. highestScore == -1 : P(-3)F(-4) / highestScore == -2 : evaluation : score(-5)로 저장됨")
    public CustomApiResponse<UpdateStudentScoreResponse> updateStudentScore(
        @AuthMember Member findMember,
        @PathVariable(name = "examId") Long examId,
        @RequestBody List<UpdateStudentScoreRequest> requests) {
        UpdateStudentScoreResponse response = examService.updateStudentScore(findMember, examId,
            requests);
        return CustomApiResponse.success(response, 200, "UPDATED-STUDENT-SCORE");
    }
}
