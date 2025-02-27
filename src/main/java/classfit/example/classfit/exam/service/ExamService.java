package classfit.example.classfit.exam.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.domain.SubClass;
import classfit.example.classfit.course.repository.MainClassRepository;
import classfit.example.classfit.course.repository.SubClassRepository;
import classfit.example.classfit.exam.domain.Exam;
import classfit.example.classfit.exam.domain.ExamScore;
import classfit.example.classfit.exam.dto.exam.request.CreateExamRequest;
import classfit.example.classfit.exam.dto.exam.request.FindExamRequest;
import classfit.example.classfit.exam.dto.exam.request.UpdateExamRequest;
import classfit.example.classfit.exam.dto.exam.response.CreateExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamResponse;
import classfit.example.classfit.exam.dto.exam.response.FindExamStudentResponse;
import classfit.example.classfit.exam.dto.exam.response.ShowExamDetailResponse;
import classfit.example.classfit.exam.dto.exam.response.UpdateExamResponse;
import classfit.example.classfit.exam.dto.process.ExamClassStudent;
import classfit.example.classfit.exam.repository.ExamRepository;
import classfit.example.classfit.exam.repository.ExamScoreRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Enrollment;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.EnrollmentRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final MainClassRepository mainClassRepository;
    private final SubClassRepository subClassRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ExamScoreRepository examScoreRepository;
    private final AcademyRepository academyRepository;


    private void validateAcademy(Member member, Long academyId) {
        Academy academy = academyRepository.findById(academyId)
                .orElseThrow(() -> new ClassfitException(ErrorCode.ACADEMY_NOT_FOUND));
        if (!Objects.equals(member.getAcademy().getId(), academyId)) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }
        if (!Objects.equals(academy.getId(), academyId)) {
            throw new ClassfitException(ErrorCode.ACADEMY_ACCESS_INVALID);
        }
    }

    @Transactional
    public CreateExamResponse createExam(@AuthMember Member findMember,
            CreateExamRequest examRequest) {
        SubClass findSubClass = subClassRepository.findById(examRequest.subClassId()).orElseThrow(
                () -> new ClassfitException(ErrorCode.SUB_CLASS_NOT_FOUND));

        MainClass findMainClass = mainClassRepository.findById(examRequest.mainClassId())
                .orElseThrow(
                        () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));
        validateAcademy(findMember, findMainClass.getAcademy().getId());

        Exam newExam = examRequest.toEntity(findSubClass, findMainClass);
        newExam.updateCreatedBy(findMember.getId());

        Exam savedExam = examRepository.save(newExam);

        return CreateExamResponse.from(savedExam);
    }

    @Transactional(readOnly = true)
    public List<FindExamStudentResponse> findExamClassStudent(@AuthMember Member findMember,
            Long examId) {
        Exam findExam = examRepository.findById(examId)
                .orElseThrow(() -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findMember.getAcademy().getId());

        List<Student> students = enrollmentRepository.findStudentsByAcademyIdAndSubClass(
                findMember.getAcademy().getId(), findExam.getSubClass().getId());

        return FindExamStudentResponse.from(students);
    }


    @Transactional(readOnly = true)
    public List<FindExamResponse> findExamList(@AuthMember Member findMember,
            FindExamRequest request) {
        validateAcademy(findMember, findMember.getAcademy().getId());

        List<Exam> exams = examRepository.findExamsByConditions(
                findMember.getAcademy().getId(),
                request.mainClassId(),
                request.subClassId(),
                request.memberName(),
                request.examName()
        );

        if (exams.isEmpty()) {
            throw new ClassfitException(ErrorCode.EXAM_NOT_FOUND);
        }

        return exams.stream()
                .map(exam -> FindExamResponse.from(exam, findMember))
                .collect(Collectors.toList());
    }

    @Transactional
    public ShowExamDetailResponse showExamDetail(@AuthMember Member findMember, Long examId) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findExam.getMainClass().getAcademy().getId());

        SubClass subClass = findExam.getSubClass();
        List<ExamScore> studentScores = findExam.getExamScores();
        List<Enrollment> enrollments = enrollmentRepository.findByAcademyIdAndSubClass(
                findMember.getAcademy().getId(), subClass);

        Integer perfectScore = studentScores.stream()
                .mapToInt(ExamScore::getScore)
                .max()
                .orElse(findExam.getHighestScore());

        Integer lowestScore = studentScores.stream()
                .mapToInt(ExamScore::getScore)
                .min()
                .orElse(0);

        Double average = studentScores.stream()
                .mapToInt(ExamScore::getScore)
                .average()
                .orElse((perfectScore + lowestScore) / 2.0);

        findExam.updateScores(lowestScore, perfectScore, average);
        examRepository.save(findExam);

        List<ExamClassStudent> examClassStudents = enrollments.stream()
                .map(classStudent -> {
                    Student student = classStudent.getStudent();
                    Integer score = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId()
                                    .equals(student.getId()))
                            .map(ExamScore::getScore)
                            .findFirst()
                            .orElse(0);

                    String evaluationDetail = examScoreRepository.findByExamAndStudentIdAndAcademyId(
                                    findMember.getAcademy().getId(), findExam, student.getId())
                            .map(ExamScore::getEvaluationDetail)
                            .orElse(null);

                    boolean checkedStudent = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId()
                                    .equals(student.getId()))
                            .map(ExamScore::isCheckedStudent)
                            .findFirst()
                            .orElse(false);

                    return ExamClassStudent.of(student.getId(), student.getName(), score,
                            findExam.getHighestScore(), evaluationDetail, checkedStudent);
                })
                .collect(Collectors.toList());

        return ShowExamDetailResponse.from(findExam, examClassStudents);
    }


    @Transactional
    public UpdateExamResponse updateExam(@AuthMember Member findMember, Long examId,
            UpdateExamRequest request) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findExam.getMainClass().getAcademy().getId());

        Integer newHighestScore = request.highestScore();
        List<ExamScore> examScores = examScoreRepository.findAllByAcademyIdAndExam(
                findMember.getAcademy().getId(), findExam);

        if (newHighestScore != null && newHighestScore > 0) {
            for (ExamScore examScore : examScores) {
                if (examScore.getScore() > newHighestScore) {
                    examScore.updateScore(0);
                    examScoreRepository.save(examScore);
                }
            }
        }

        List<String> examRangeList = request.examRange();
        findExam.updateExam(request.examDate(), request.standard(), newHighestScore,
                request.examPeriod(), request.examName(), examRangeList);
        examRepository.save(findExam);

        return UpdateExamResponse.from(findExam);
    }


    @Transactional
    public void deleteExam(@AuthMember Member findMember, Long examId) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findExam.getMainClass().getAcademy().getId());
        examRepository.delete(findExam);
    }
}
