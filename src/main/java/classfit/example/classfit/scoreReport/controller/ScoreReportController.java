package classfit.example.classfit.scoreReport.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.controller.docs.ScoreReportControllerDocs;
import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.request.SentStudentOpinionRequest;
import classfit.example.classfit.scoreReport.dto.response.CreateReportResponse;
import classfit.example.classfit.scoreReport.dto.response.FindClassStudent;
import classfit.example.classfit.scoreReport.dto.response.FindReportResponse;
import classfit.example.classfit.scoreReport.dto.response.SentStudentOpinionResponse;
import classfit.example.classfit.scoreReport.dto.response.ShowStudentReportResponse;
import classfit.example.classfit.scoreReport.service.ScoreReportService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return CustomApiResponse.success(response, 201, "학습 리포트 생성 성공");
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
        List<ReportExam> exams = scoreReportService.showReportExam(member, startDate, endDate,
                mainClassId, subClassId);
        return CustomApiResponse.success(exams, 200, "기간 내 시험지 조회 성공");
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
                subClassId);
        return CustomApiResponse.success(response, 200, "학생 리포트 검색 성공");
    }

    @Override
    @DeleteMapping("/{student-report-id}")
    public CustomApiResponse<Void> deleteStudentReport(
            @AuthMember Member member,
            @PathVariable(name = "student-report-id") Long studentReportId
    ) {
        scoreReportService.deleteReport(member, studentReportId);
        return CustomApiResponse.success(null, 200, "학생 학습리포트 삭제 성공");
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
        return CustomApiResponse.success(response, 200, "클래스 별 학생 조회 성공");
    }

    @Override
    @PatchMapping("/student-opinion")
    public CustomApiResponse<List<SentStudentOpinionResponse>> sentStudentOpinion(
            @AuthMember Member member,
            @RequestBody List<SentStudentOpinionRequest> requests
    ) {
        List<SentStudentOpinionResponse> response = scoreReportService.sentStudentOpinion(member,
                requests);
        return CustomApiResponse.success(response, 200, "학생의 개인 의견 전송 성공");
    }

    @Override
    @GetMapping("/{student-report-id}")
    public CustomApiResponse<ShowStudentReportResponse> showStudentReport(
            @AuthMember Member member,
            @PathVariable(name = "student-report-id") Long reportId
    ) {
        ShowStudentReportResponse response = scoreReportService.showStudentReport(member, reportId);
        return CustomApiResponse.success(response, 200, "학생 리포트 상세조회 성공");
    }

    @Override
    @GetMapping("/all-report")
    public CustomApiResponse<List<FindReportResponse>> findAllReport(@AuthMember Member member) {
        List<FindReportResponse> response = scoreReportService.findAllReport(member);
        return CustomApiResponse.success(response, 200, "학생 리포트 전체조회 성공");
    }
}
