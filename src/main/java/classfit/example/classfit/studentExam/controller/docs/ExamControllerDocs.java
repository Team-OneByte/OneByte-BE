package classfit.example.classfit.studentExam.controller.docs;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import classfit.example.classfit.studentExam.dto.request.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.request.FindExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateStudentScoreRequest;
import classfit.example.classfit.studentExam.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "시험 컨트롤러", description = "시험과 관련된 API입니다.")
public interface ExamControllerDocs {

    @Operation(summary = "시험 정보 등록", description = "시험 정보 등록하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "201", description = "시험 정보 등록 성공")
    })
    CustomApiResponse<CreateExamResponse> createExam(
        @AuthMember Member findMember,
        @RequestBody CreateExamRequest req
    );

    @Operation(summary = "시험 등록 시 해당 클래스 학생 조회", description = "시험 등록시 해당 클래스 학생 조회하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "해당 클래스 학생 조회 성공")
    })
    CustomApiResponse<List<ExamClassStudent>> findExamClassStudent(
        @AuthMember Member findMember,
        @PathVariable Long examId
    );

    @Operation(summary = "시험 리스트 조회", description = "시험 이름과 작성자로 시험 검색하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "시험 리스트 조회 성공")
    })
    CustomApiResponse<List<FindExamResponse>> findExamList(
        @AuthMember Member findMember,
        @RequestBody FindExamRequest request
    );

    @Operation(summary = "시험 상세 조회", description = "시험 상세 조회 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "시험 상세 조회 성공")
    })
    CustomApiResponse<ShowExamDetailResponse> showExamDetail(
        @AuthMember Member findMember,
        @PathVariable Long examId
    );

    @Operation(summary = "시험 수정", description = "시험 수정 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "시험 수정 성공")
    })
    CustomApiResponse<UpdateExamResponse> updateExam(
        @AuthMember Member findMember,
        @PathVariable Long examId,
        @RequestBody UpdateExamRequest request
    );

    @Operation(summary = "시험 삭제", description = "시험 삭제 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "시험 삭제 성공")
    })
    ResponseEntity<CustomApiResponse> deleteExam(
        @AuthMember Member findMember,
        @PathVariable Long examId
    );

    @Operation(summary = "학생 시험 점수 수정", description = "학생 성적 수정하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "학생 시험 점수 수정 성공")
    })
    CustomApiResponse<UpdateStudentScoreResponse> updateStudentScore(
        @AuthMember Member findMember,
        @PathVariable Long examId,
        @RequestBody List<UpdateStudentScoreRequest> requests
    );
}
