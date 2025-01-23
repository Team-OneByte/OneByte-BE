package classfit.example.classfit.scoreReport.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.controller.docs.ScoreReportControllerDocs;
import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.request.SentStudentOpinionRequest;
import classfit.example.classfit.scoreReport.dto.response.*;
import classfit.example.classfit.scoreReport.service.ScoreReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ScoreReportController implements ScoreReportControllerDocs {

    private final ScoreReportService scoreReportService;

    @Override
    @PostMapping
    public CustomApiResponse<CreateReportResponse> createReport(
        @AuthMember Member member,
        @RequestBody @Valid CreateReportRequest request
    ) {
        CreateReportResponse response = scoreReportService.createReport(member, request);
        return CustomApiResponse.success(response, 201, "CREATED-REPORT");
    }

    @Override
    @GetMapping("/exam-list")
    public CustomApiResponse<List<ReportExam>> findExamList(
        @AuthMember Member member,
        @RequestParam("startDate") LocalDate startDate,
        @RequestParam("endDate") LocalDate endDate,
        @RequestParam("mainClassId") Long mainClassId,
        @RequestParam("subClassId") Long subClassId
    ) {
        List<ReportExam> exams = scoreReportService.showReportExam(member, startDate, endDate, mainClassId,
            subClassId);
        return CustomApiResponse.success(exams, 200, "FIND-REPORT-EXAM");
    }

    @Override
    @GetMapping("/student-report")
    public CustomApiResponse<List<FindReportResponse>> findReport(
        @AuthMember Member member,
        @RequestParam(name = "mainClassId", required = false) Long mainClassId,
        @RequestParam(name = "subClassId", required = false) Long subClassId,
        @RequestParam(name = "memberName", required = false) String memberName
    ) {
        List<FindReportResponse> response = scoreReportService.findReport(member, mainClassId,
            subClassId, memberName);
        return CustomApiResponse.success(response, 200, "FIND-STUDENT-REPORT");
    }

    @Override
    @DeleteMapping("/{student-report-id}")
    public CustomApiResponse<Void> deleteStudentReport(
        @AuthMember Member member,
        @PathVariable(name = "student-report-id") Long studentReportId
    ) {
        scoreReportService.deleteReport(member, studentReportId);
        return CustomApiResponse.success(null, 200, "DELETED-STUDENT-REPORT");
    }

    @Override
    @GetMapping("/class-student")
    public CustomApiResponse<List<FindClassStudent>> findClassStudent(
        @AuthMember Member member,
        @RequestParam(name = "mainClassId") Long mainClassId,
        @RequestParam(name = "subClassId") Long subClassId
    ) {
        List<FindClassStudent> response = scoreReportService.findClassStudents(member, mainClassId,
            subClassId);
        return CustomApiResponse.success(response, 200, "FIND-CLASS-STUDENT");
    }

    @Override
    @PatchMapping("/student-opinion")
    public CustomApiResponse<List<SentStudentOpinionResponse>> sentStudentOpinion(
        @AuthMember Member member,
        @RequestBody List<SentStudentOpinionRequest> requests
    ) {
        List<SentStudentOpinionResponse> response = scoreReportService.sentStudentOpinion(member,
            requests);
        return CustomApiResponse.success(response, 200, "SENT-STUDENT-OPINION");
    }

    @Override
    @GetMapping("/{student-report-id}")
    public CustomApiResponse<ShowStudentReportResponse> showStudentReport(
        @AuthMember Member member,
        @PathVariable(name = "student-report-id") Long reportId
    ) {
        ShowStudentReportResponse response = scoreReportService.showStudentReport(member, reportId);
        return CustomApiResponse.success(response, 200, "SHOW-STUDENT-REPORT");
    }

    @Override
    @GetMapping("/all-report")
    public CustomApiResponse<List<FindAllReportResponse>> findAllReport(@AuthMember Member member) {
        List<FindAllReportResponse> response = scoreReportService.findAllReport(member);
        return CustomApiResponse.success(response, 200, "FIND-ALL-STUDENT-REPORTS");
    }
}
