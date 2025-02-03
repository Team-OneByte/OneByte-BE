package classfit.example.classfit.scoreReport.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.scoreReport.dto.process.ReportExam;
import classfit.example.classfit.scoreReport.dto.request.CreateReportRequest;
import classfit.example.classfit.scoreReport.dto.request.SentStudentOpinionRequest;
import classfit.example.classfit.scoreReport.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "학습 리포트 컨트롤러", description = "학습 리포트 API 입니다.")
public interface ScoreReportControllerDocs {

    @Operation(summary = "학습 리포트 생성", description = "학습 리포트를 생성하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "201", description = "학습 리포트 생성 성공")
    })
    CustomApiResponse<CreateReportResponse> createReport(
            @AuthMember Member member,
            @RequestBody CreateReportRequest request
    );

    @Operation(summary = "기간 내 시험지 조회", description = "학습 리포트 생성 시 기간 내 시험지 조회 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "기간 내 시험지 조회 성공")
    })
    CustomApiResponse<List<ReportExam>> findExamList(
            @AuthMember Member member,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam Long mainClassId,
            @RequestParam Long subClassId
    );

    @Operation(summary = "학생 리포트 검색", description = "학습 리포트 검색 조회 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "학생 리포트 검색 성공")
    })
    CustomApiResponse<List<FindReportResponse>> findReport(
            @AuthMember Member member,
            @RequestParam(required = false) Long mainClassId,
            @RequestParam(required = false) Long subClassId,
            @RequestParam(required = false) String memberName
    );

    @Operation(summary = "학생 학습리포트 삭제", description = "학생 학습리포트 삭제 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "학생 학습리포트 삭제 성공")
    })
    CustomApiResponse<Void> deleteStudentReport(
            @AuthMember Member member,
            @PathVariable Long studentReportId
    );

    @Operation(summary = "클래스 별 학생 조회", description = "클래스 별 학생 조회 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "클래스 별 학생 조회 성공")
    })
    CustomApiResponse<List<FindClassStudent>> findClassStudent(
            @AuthMember Member member,
            @RequestParam Long mainClassId,
            @RequestParam Long subClassId
    );

    @Operation(summary = "학생의 개인 의견 전송", description = "학습리포트에 개인의견 전송 API입니다. 학습 리포트 생성 후에 사용가능.", responses = {
            @ApiResponse(responseCode = "200", description = "학생의 개인 의견 전송 성공")
    })
    CustomApiResponse<List<SentStudentOpinionResponse>> sentStudentOpinion(
            @AuthMember Member member,
            @RequestBody List<SentStudentOpinionRequest> requests
    );

    @Operation(summary = "학생 리포트 상세조회", description = "학생의 학습리포트 상세조회 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "학생 리포트 상세조회 성공")
    })
    CustomApiResponse<ShowStudentReportResponse> showStudentReport(
            @AuthMember Member member,
            @PathVariable Long reportId
    );

    @Operation(summary = "학생 리포트 전체조회", description = "학습 리포트 생성 성적 리포트 전체조회 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "학생 리포트 전체조회 성공")
    })
    CustomApiResponse<List<FindReportResponse>> findAllReport(@AuthMember Member member);
}
