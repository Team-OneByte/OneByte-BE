package classfit.example.classfit.studentExam.controller;

import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import classfit.example.classfit.studentExam.dto.request.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.request.FindExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateExamRequest;
import classfit.example.classfit.studentExam.dto.response.CreateExamResponse;
import classfit.example.classfit.studentExam.dto.response.FindExamResponse;
import classfit.example.classfit.studentExam.dto.response.ShowExamDetailResponse;
import classfit.example.classfit.studentExam.dto.response.UpdateExamResponse;
import classfit.example.classfit.studentExam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exam")
@RequiredArgsConstructor
@Tag(name = "성적관리 API", description = "성적관리 API입니다.")
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @Operation(summary = "시험 정보 등록", description = "시험 정보 등록하는 API 입니다.")
    public ApiResponse<CreateExamResponse> createExam(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @RequestBody CreateExamRequest req) {
        CreateExamResponse response = examService.createExam(memberId, req);
        return ApiResponse.success(response, 201, "CREATED EXAM");
    }

    @GetMapping("/{examId}")
    @Operation(summary = "시험 등록 시 해당 클래스 학생 조회", description = "시험 등록시 해당 클래스 학생 조회하는 API 입니다.")
    public ApiResponse<List<ExamClassStudent>> findExamClassStuent(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @PathVariable(name = "examId") Long examId
    ) {
        List<ExamClassStudent> response = examService.findExamClassStuent(memberId,
                examId);
        return ApiResponse.success(response, 200, "FIND EXAM-STUDENT");
    }

    @PostMapping("/findexam")
    @Operation(summary = "시험 리스트 조회", description = "시험 이름과 작성자로 시험 검색하는 API 입니다.")
    public ApiResponse<List<FindExamResponse>> findExamList(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @RequestBody FindExamRequest request
    ) {
        List<FindExamResponse> response = examService.findExamList(memberId, request);
        return ApiResponse.success(response, 200, "FIND EXAM-LIST");
    }

    @GetMapping("/findexam/{examId}")
    @Operation(summary = "시험 상세 조회", description = "시험 상세 내용 조회 API 입니다.")
    public ApiResponse<ShowExamDetailResponse> showExamDetail(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @PathVariable(name = "examId") Long examId) {
        ShowExamDetailResponse response = examService.showExamDetail(memberId, examId);

        return ApiResponse.success(response, 200, "FIND EXAM");
    }

    @PutMapping("/{examId}")
    @Operation(summary = "시험 수정", description = "시험 수정하는 API 입니다.")
    public ApiResponse<UpdateExamResponse> updateExam(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @PathVariable(name = "examId") Long examId,
            @RequestBody UpdateExamRequest request) {
        UpdateExamResponse response = examService.updateExam(memberId, examId, request);
        return ApiResponse.success(response, 200, "UPDATED EXAM");
    }

    @DeleteMapping("/{examId}")
    @Operation(summary = "시험 삭제", description = "시험 삭제하는 API 입니다.")
    public ResponseEntity<ApiResponse> deleteExam(
            @RequestHeader(name = "member-no", required = false) Long memberId,
            @PathVariable(name = "examId") Long examId) {
        examService.deleteExam(memberId, examId);
        return ResponseEntity.ok(ApiResponse.success(null,200, "DELETED EXAM"));
    }

}
