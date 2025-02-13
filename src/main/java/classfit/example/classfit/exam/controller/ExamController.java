package classfit.example.classfit.exam.controller;


import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.exam.controller.docs.ExamControllerDocs;
import classfit.example.classfit.exam.dto.exam.request.CreateExamRequest;
import classfit.example.classfit.exam.dto.exam.request.FindExamRequest;
import classfit.example.classfit.exam.dto.exam.request.UpdateExamRequest;
import classfit.example.classfit.exam.dto.exam.response.CreateExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamStudentResponse;
import classfit.example.classfit.exam.dto.exam.response.ShowExamDetailResponse;
import classfit.example.classfit.exam.dto.exam.response.UpdateExamResponse;
import classfit.example.classfit.exam.service.ExamService;
import classfit.example.classfit.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/exam")
@RequiredArgsConstructor
public class ExamController implements ExamControllerDocs {

    private final ExamService examService;

    @Override
    @PostMapping
    public CustomApiResponse<CreateExamResponse> createExam(
            @AuthMember Member findMember,
            @RequestBody CreateExamRequest req
    ) {
        CreateExamResponse response = examService.createExam(findMember, req);
        return CustomApiResponse.success(response, 201, "시험 정보 등록 성공");
    }

    @Override
    @GetMapping("/{examId}")
    public CustomApiResponse<List<FindExamStudentResponse>> findExamClassStudent(
            @AuthMember Member findMember,
            @PathVariable(name = "examId") Long examId
    ) {
        List<FindExamStudentResponse> response = examService.findExamClassStudent(findMember,
                examId);
        return CustomApiResponse.success(response, 200, "해당 클래스 학생 조회 성공");
    }

    @Override
    @PostMapping("/find-exam")
    public CustomApiResponse<List<FindExamResponse>> findExamList(
            @AuthMember Member findMember,
            @RequestBody FindExamRequest request
    ) {
        List<FindExamResponse> response = examService.findExamList(findMember, request);
        return CustomApiResponse.success(response, 200, "시험 리스트 조회 성공");
    }

    @Override
    @GetMapping("/find-exam/{examId}")
    public CustomApiResponse<ShowExamDetailResponse> showExamDetail(
            @AuthMember Member findMember,
            @PathVariable(name = "examId") Long examId
    ) {
        ShowExamDetailResponse response = examService.showExamDetail(findMember, examId);
        return CustomApiResponse.success(response, 200, "시험 상세 조회 성공");
    }

    @Override
    @PutMapping("/{examId}")
    public CustomApiResponse<UpdateExamResponse> updateExam(
            @AuthMember Member findMember,
            @PathVariable(name = "examId") Long examId,
            @RequestBody UpdateExamRequest request
    ) {
        UpdateExamResponse response = examService.updateExam(findMember, examId, request);
        return CustomApiResponse.success(response, 200, "시험 수정 성공");
    }

    @Override
    @DeleteMapping("/{examId}")
    public ResponseEntity<CustomApiResponse> deleteExam(
            @AuthMember Member findMember,
            @PathVariable(name = "examId") Long examId
    ) {
        examService.deleteExam(findMember, examId);
        return ResponseEntity.ok(CustomApiResponse.success(null, 200, "시험 삭제 성공"));
    }

}
