package classfit.example.classfit.student.controller.docs;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.dto.request.StudentRequest;
import classfit.example.classfit.student.dto.request.StudentUpdateRequest;
import classfit.example.classfit.student.dto.response.StudentInfoResponse;
import classfit.example.classfit.student.dto.response.StudentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "학생 컨트롤러", description = "학생 관련 API")
public interface StudentControllerDocs {

    @Operation(summary = "학생 정보 등록", description = "학생 정보를 등록하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "201", description = "학생 정보 등록 성공")
    })
    CustomApiResponse<StudentResponse> registerStudent(@RequestBody StudentRequest req);

    @Operation(summary = "전체 학생 정보 조회", description = "전체 학생 정보를 조회하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "전체 학생 정보 조회 성공")
    })
    CustomApiResponse<List<StudentResponse>> studentInfoAll(@AuthMember Member member);

    @Operation(summary = "학생 정보 삭제", description = "학생 정보를 삭제하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "학생 정보 삭제 성공")
    })
    CustomApiResponse<List<Long>> deleteStudent(
        @AuthMember Member member,
        @RequestParam List<Long> studentIds
    );

    @Operation(summary = "학생 정보 수정", description = "학생 정보를 수정하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "학생 정보 수정 성공")
    })
    CustomApiResponse<Long> updateStudent(
        @PathVariable("studentId") Long studentId,
        @RequestBody StudentUpdateRequest req
    );

    @Operation(summary = "개별 학생 정보 조회", description = "특정 학생 정보를 조회하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "개별 학생 정보 조회 성공")
    })
    CustomApiResponse<StudentInfoResponse> studentInfo(@PathVariable("studentId") Long studentId);

    @Operation(summary = "학생 이름 검색", description = "특정 학생 이름으로 목록 조회하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "학생 이름 검색 성공")
    })
    CustomApiResponse<List<StudentResponse>> findStudentByName(@RequestParam String studentName);
}
