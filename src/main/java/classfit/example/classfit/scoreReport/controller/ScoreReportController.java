package classfit.example.classfit.scoreReport.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.request.SentStudentOpinionRequest;
import classfit.example.classfit.scoreReport.dto.response.*;
import classfit.example.classfit.scoreReport.service.ScoreReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@Tag(name = "학습 리포트 API", description = "학습 리포트 API 입니다.")
public class ScoreReportController {

    private final ScoreReportService scoreReportService;

    @PostMapping
    @Operation(summary = "학습 리포트 생성", description = "학습 리포트 생성하는 API 입니다.")
    public CustomApiResponse<CreateReportResponse> createReport(@AuthMember Member member,
                                                                @RequestBody CreateReportRequest request) {
        CreateReportResponse response = scoreReportService.createReport(member, request);
        return CustomApiResponse.success(response, 201, "CREATED-REPORT");
    }

    @GetMapping("/exam-list")
    @Operation(summary = "기간 내 시험지 조회", description = "학습 리포트 생성 시 기간 내 시험지 조회 API 입니다.")
    public CustomApiResponse<List<ReportExam>> findExamList(
        @AuthMember Member member,
        @RequestParam("startDate") LocalDate startDate,
        @RequestParam("endDate") LocalDate endDate,
        @RequestParam("mainClassId") Long mainClassId,
        @RequestParam("subClassId") Long subClassId) {
        List<ReportExam> exams = scoreReportService.showReportExam(member, startDate, endDate, mainClassId,
            subClassId);
        return CustomApiResponse.success(exams, 200, "FIND-REPORT-EXAM");
    }

    @GetMapping("/student-report")
    @Operation(summary = "학생 학습리포트 검색", description = "학습 리포트 검색,조회 API입니다. memberName,mainClass,subClass로 검색 가능.")
    public CustomApiResponse<List<FindReportResponse>> findReport(@AuthMember Member member,
                                                                  @RequestParam(name = "mainClassId", required = false) Long mainClassId,
                                                                  @RequestParam(name = "subClassId", required = false) Long subClassId,
                                                                  @RequestParam(name = "memberName", required = false) String memberName) {
        List<FindReportResponse> response = scoreReportService.findReport(member, mainClassId,
            subClassId, memberName);
        return CustomApiResponse.success(response, 200, "FIND-STUDENT-REPORT");
    }

    @DeleteMapping("/{student-report-id}")
    @Operation(summary = "학습리포트 삭제", description = "학생 학습리포트 삭제 API입니다.")
    public CustomApiResponse<?> deleteStudentReport(@AuthMember Member member,
                                                    @PathVariable(name = "student-report-id") Long studentReportId) {
        scoreReportService.deleteReport(member, studentReportId);
        return CustomApiResponse.success(null, 200, "DELETED-STUDENT-REPORT");
    }

    @GetMapping("/class-student")
    @Operation(summary = "클래스 별 학생 조회", description = "클래스 별 학생 조회 API 입니다.")
    public CustomApiResponse<List<FindClassStudent>> findClassStudent(@AuthMember Member member,
                                                                      @RequestParam(name = "mainClassId") Long mainClassId,
                                                                      @RequestParam(name = "subClassId") Long subClassId) {
        List<FindClassStudent> response = scoreReportService.findClassStudents(member, mainClassId,
            subClassId);
        return CustomApiResponse.success(response, 200, "FIND-CLASS-STUDENT");
    }

    @PatchMapping("/student-opinion")
    @Operation(summary = "학생리포트에 개인의견 전송", description = "학생에게 개인의견 전송 API입니다. 학습 리포트 생성 후에 사용가능.")
    public CustomApiResponse<List<SentStudentOpinionResponse>> sentStudentOpinion(
        @AuthMember Member member,
        @RequestBody List<SentStudentOpinionRequest> requests) {
        List<SentStudentOpinionResponse> response = scoreReportService.sentStudentOpinion(member,
            requests);
        return CustomApiResponse.success(response, 200, "SENT-STUDENT-OPINION");
    }

    @GetMapping("/{student-report-id}")
    @Operation(summary = "학생 리포트 상세조회", description = "학생의 학습리포트 상세조회 API입니다.")
    public CustomApiResponse<ShowStudentReportResponse> showStudentReport(@AuthMember Member member,
                                                                          @PathVariable(name = "student-report-id") Long reportId) {
        ShowStudentReportResponse response = scoreReportService.showStudentReport(member, reportId);
        return CustomApiResponse.success(response, 200, "SHOW-STUDENT-REPORT");
    }

    @GetMapping("/all-report")
    @Operation(summary = "학습 리포트 전체조회", description = "학원 별 생성한 성적 리포트 전체조회 API입니다.")
    public CustomApiResponse<List<FindAllReportResponse>> findAllReport(
        @AuthMember Member member) {

        List<FindAllReportResponse> response = scoreReportService.findAllReport(member);

        return CustomApiResponse.success(response, 200, "FIND-ALL-STUDENT-REPORTS");
    }


}
