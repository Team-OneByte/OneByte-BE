package classfit.example.classfit.scoreReport.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.response.CreateReportResponse;
import classfit.example.classfit.scoreReport.service.ScoreReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@Tag(name ="성적 리포트 API",description = "성적 리포트 API 입니다.")
public class ScoreReportController {
    private final ScoreReportService scoreReportService;

    @PostMapping
    @Operation(summary = "성적 리포트 생성", description = "성적 리포트 생성하는 API 입니다.")
    public ApiResponse<CreateReportResponse> createReport( @AuthMember Member member,@RequestBody
            CreateReportRequest request) {
        CreateReportResponse response = scoreReportService.createReport(member,request);
        return ApiResponse.success(response,201,"CREATED-REPORT");
    }
}
