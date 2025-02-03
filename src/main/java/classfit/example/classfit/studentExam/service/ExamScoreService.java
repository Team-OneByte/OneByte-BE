package classfit.example.classfit.studentExam.service;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamRepository;
import classfit.example.classfit.studentExam.domain.ExamScore;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import classfit.example.classfit.studentExam.domain.StudentExamScoreRepository;
import classfit.example.classfit.studentExam.dto.examScoreRequest.CreateExamScoreRequest;
import classfit.example.classfit.studentExam.dto.examScoreResponse.CreateExamScoreResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamScoreService {

    private final ExamRepository examRepository;
    private final ClassStudentRepository classStudentRepository;
    private final StudentExamScoreRepository studentExamScoreRepository;

    @Transactional
    public List<CreateExamScoreResponse> createExamScore(@AuthMember Member findMember,
            CreateExamScoreRequest request) {
        Exam findExam = examRepository.findById(request.examId())
                .orElseThrow(() -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));

        List<ClassStudent> classStudents = classStudentRepository.findByAcademyIdAndSubClass(
                findMember.getAcademy().getId(), findExam.getSubClass());
        List<ExamScore> examScores = classStudents.stream()
                .map(classStudent -> request.toEntity(classStudent.getStudent(), findExam))
                .toList();
        if (findExam.getStandard() == Standard.PF) {
            examScores.forEach(
                    examScore -> examScore.updateStandardStatus(StandardStatus.PASS));
        } else if (findExam.getStandard() == Standard.EVALUATION) {
            examScores.forEach(
                    examScore -> examScore.updateStandardStatus(StandardStatus.EVALUATION));
        }
        studentExamScoreRepository.saveAll(examScores);
        return CreateExamScoreResponse.from(examScores);
    }
}
