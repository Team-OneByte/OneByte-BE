package classfit.example.classfit.exam.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.exam.dto.exam.request.CreateExamRequest;
import classfit.example.classfit.exam.dto.exam.request.FindExamRequest;
import classfit.example.classfit.exam.dto.exam.request.UpdateExamRequest;
import classfit.example.classfit.exam.dto.exam.response.CreateExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamStudentResponse;
import classfit.example.classfit.exam.dto.exam.response.ShowExamDetailResponse;
import classfit.example.classfit.exam.dto.exam.response.UpdateExamResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
    CustomApiResponse<List<FindExamStudentResponse>> findExamClassStudent(
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

}
