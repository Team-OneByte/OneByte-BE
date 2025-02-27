package classfit.example.classfit.exam.service;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Enrollment;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.EnrollmentRepository;
import classfit.example.classfit.exam.domain.Exam;
import classfit.example.classfit.exam.repository.ExamRepository;
import classfit.example.classfit.exam.domain.ExamScore;
import classfit.example.classfit.exam.domain.enumType.Standard;
import classfit.example.classfit.exam.domain.enumType.StandardStatus;
import classfit.example.classfit.exam.repository.ExamScoreRepository;
import classfit.example.classfit.exam.dto.examscore.request.CreateExamScoreRequest;
import classfit.example.classfit.exam.dto.examscore.request.UpdateExamScoreRequest;
import classfit.example.classfit.exam.dto.examscore.response.CreateExamScoreResponse;
import classfit.example.classfit.exam.dto.examscore.response.UpdateExamScoreResponse;
import classfit.example.classfit.exam.dto.process.ExamStudent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamScoreService {

    private final ExamRepository examRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ExamScoreRepository examScoreRepository;

    @Transactional
    public List<CreateExamScoreResponse> createExamScore(
            @AuthMember Member findMember,
            List<CreateExamScoreRequest> requests
    ) {
        Exam findExam = examRepository.findById(requests.get(0).examId())
                .orElseThrow(() -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));

        for (CreateExamScoreRequest request : requests) {
            if (request.isScoreRequired(findExam) && request.score() == null) {
                throw new ClassfitException(ErrorCode.SCORE_LESS_THAN_ZERO);
            }
        }

        List<Enrollment> enrollments = enrollmentRepository.findByAcademyIdAndSubClass(
                findMember.getAcademy().getId(), findExam.getSubClass());

        List<ExamScore> examScores = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            CreateExamScoreRequest studentRequest = requests.stream()
                    .filter(req -> req.studentId().equals(enrollment.getStudent().getId()))
                    .findFirst()
                    .orElseThrow(() -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));

            ExamScore newExamScore = ExamScore.toEntity(
                    enrollment.getStudent(), findExam, studentRequest.standardStatus(),
                    studentRequest.checkedStudent());

            examScoreRepository.save(newExamScore);

            examScores.add(newExamScore);
        }

        return CreateExamScoreResponse.from(examScores);
    }


    @Transactional
    public UpdateExamScoreResponse updateExamScore(
            @AuthMember Member findMember, Long examId,
            List<UpdateExamScoreRequest> requests
    ) {
        Exam findExam = examRepository.findById(examId)
                .orElseThrow(() -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));

        for (UpdateExamScoreRequest request : requests) {
            ExamScore examScore = examScoreRepository.findByExamAndStudentIdAndAcademyId(
                            findMember.getAcademy().getId(), findExam, request.studentId())
                    .orElseThrow(() -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));

            examScore.updateScore(request.score());
            examScore.updateCheckedStudent(request.checkedStudent());

            if (findExam.getStandard() == Standard.PF) {
                examScore.updateStandardStatus(request.standardStatus());
            } else if (findExam.getStandard() == Standard.EVALUATION) {
                examScore.updateEvaluationDetail(request.evaluationDetail());
            }

            examScoreRepository.save(examScore);
        }

        List<Enrollment> enrollments = enrollmentRepository.findByAcademyIdAndSubClass(
                findMember.getAcademy().getId(), findExam.getSubClass());

        List<ExamStudent> examStudents = enrollments.stream().map(enrollment -> {
            Student student = enrollment.getStudent();
            Integer score = examScoreRepository.findByExamAndStudentIdAndAcademyId(
                            findMember.getAcademy().getId(), findExam, student.getId())
                    .map(ExamScore::getScore).orElse(null);

            StandardStatus standardStatus = requests.stream()
                    .filter(request -> request.studentId().equals(student.getId()))
                    .map(UpdateExamScoreRequest::standardStatus).findFirst().orElse(null);

            String evaluationDetail = examScoreRepository.findByExamAndStudentIdAndAcademyId(
                            findMember.getAcademy().getId(), findExam, student.getId())
                    .map(ExamScore::getEvaluationDetail).orElse(null);

            boolean checkedStudent = requests.stream()
                    .filter(request -> request.studentId().equals(student.getId()))
                    .map(UpdateExamScoreRequest::checkedStudent).findFirst().orElse(false);

            return ExamStudent.of(student.getId(), student.getName(), score, standardStatus,
                    evaluationDetail, checkedStudent);
        }).collect(Collectors.toList());

        return UpdateExamScoreResponse.of(findExam.getHighestScore(), examStudents);
    }

}
