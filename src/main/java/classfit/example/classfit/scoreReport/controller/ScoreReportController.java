package classfit.example.classfit.scoreReport.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.response.CreateReportResponse;
import classfit.example.classfit.scoreReport.dto.response.FindReportResponse;
import classfit.example.classfit.scoreReport.service.ScoreReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@Tag(name = "학습 리포트 API", description = "성적 리포트 API 입니다.")
public class ScoreReportController {

    private final ScoreReportService scoreReportService;

    @PostMapping
    @Operation(summary = "학습 리포트 생성", description = "학습 리포트 생성하는 API 입니다.")
    public ApiResponse<CreateReportResponse> createReport(@AuthMember Member member, @RequestBody
    CreateReportRequest request) {
        CreateReportResponse response = scoreReportService.createReport(member, request);
        return ApiResponse.success(response, 201, "CREATED-REPORT");
    }

    @GetMapping("/exam-list")
    @Operation(summary = "기간 내 시험지 조회", description = "학습 리포트 생성 시 기간 내 시험지 조회 API 입니다.")
    public ApiResponse<List<ReportExam>> findExamList(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        List<ReportExam> exams = scoreReportService.showReportExam(startDate, endDate);
        return ApiResponse.success(exams, 200, "FIND-REPORT-EXAM");
    }

    @GetMapping("/student-report")
    @Operation(summary = "학생 학습리포트", description = "학습 리포트 검색,조회 API입니다. memberName,mainClass,subClass로 검색 가능.")
    public ApiResponse<List<FindReportResponse>> findReport(@AuthMember Member member,
            @RequestParam(name = "mainClassId") Long mainClassId,
            @RequestParam(name = "subClassId") Long subClassId,
            @RequestParam(name = "memberName") String memberName) {
        List<FindReportResponse> response = scoreReportService.findReport(member, mainClassId,
                subClassId, memberName);
        return ApiResponse.success(response, 200, "FIND-STUDENT-REPORT");
    }

//    @DeleteMapping("/{student-report-id}")
//    @Operation(summary = "학습리포트 삭제", description = "학생 학습리포트 삭제 API입니다.")
//    public ApiResponse<?> deleteStudentReport(@AuthMember Member member,
//            @PathVariable(name = "student-report-id")Long studentReportId) {
//        scoreReportService.deleteStudentReport(member,studentReportId);
//        return ApiResponse.success(null,200,"DELETED-STUDENT-REPORT");
//    }
}
