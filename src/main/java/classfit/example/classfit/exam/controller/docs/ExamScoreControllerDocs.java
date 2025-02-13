package classfit.example.classfit.exam.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.exam.dto.examscore.request.CreateExamScoreRequest;
import classfit.example.classfit.exam.dto.examscore.request.UpdateExamScoreRequest;
import classfit.example.classfit.exam.dto.examscore.response.CreateExamScoreResponse;
import classfit.example.classfit.exam.dto.examscore.response.UpdateExamScoreResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "학생 시험 점수 컨트롤러", description = "학생의 시험점수와 관련된 API입니다.")
public interface ExamScoreControllerDocs {

    @Operation(summary = "학생 시험 성적 등록", description = "시험지 생성 후 학생 시험 성적 등록하는 API입니다.", responses = {
            @ApiResponse(responseCode = "201", description = "학생 시험 성적 등록 성공")
    })
    CustomApiResponse<List<CreateExamScoreResponse>> createExamScore(
            @AuthMember Member findMember,
            @RequestBody List<CreateExamScoreRequest> req
    );

    @Operation(summary = "학생 시험 점수 수정", description = "학생 성적 수정하는 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "학생 시험 점수 수정 성공")
    })
    CustomApiResponse<UpdateExamScoreResponse> updateStudentScore(
            @AuthMember Member findMember,
            @PathVariable Long examId,
            @RequestBody List<UpdateExamScoreRequest> requests
    );
}
