package classfit.example.classfit.studentExam.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.course.domain.MainClass;
import classfit.example.classfit.course.domain.SubClass;
import classfit.example.classfit.course.repository.MainClassRepository;
import classfit.example.classfit.course.repository.SubClassRepository;
import classfit.example.classfit.student.domain.Enrollment;
import classfit.example.classfit.student.repository.EnrollmentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamRepository;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.domain.StudentExamScore;
import classfit.example.classfit.studentExam.domain.StudentExamScoreRepository;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import classfit.example.classfit.studentExam.dto.process.ExamStudent;
import classfit.example.classfit.studentExam.dto.request.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.request.FindExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateExamRequest;
import classfit.example.classfit.studentExam.dto.request.UpdateStudentScoreRequest;
import classfit.example.classfit.studentExam.dto.response.CreateExamResponse;
import classfit.example.classfit.studentExam.dto.response.FindExamResponse;
import classfit.example.classfit.studentExam.dto.response.ShowExamDetailResponse;
import classfit.example.classfit.studentExam.dto.response.UpdateExamResponse;
import classfit.example.classfit.studentExam.dto.response.UpdateStudentScoreResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final MainClassRepository mainClassRepository;
    private final SubClassRepository subClassRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentExamScoreRepository studentExamScoreRepository;
    private final AcademyRepository academyRepository;
    private final StudentRepository studentRepository;


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
    public CreateExamResponse createExam(@AuthMember Member findMember, CreateExamRequest request) {
        SubClass findSubClass = subClassRepository.findById(request.subClassId()).orElseThrow(
                () -> new ClassfitException(ErrorCode.SUB_CLASS_NOT_FOUND));

        MainClass findMainClass = mainClassRepository.findById(request.mainClassId()).orElseThrow(
                () -> new ClassfitException(ErrorCode.MAIN_CLASS_NOT_FOUND));
        validateAcademy(findMember, findMainClass.getAcademy().getId());

        Exam newExam = request.toEntity(findSubClass, findMainClass);
        newExam.updateCreatedBy(findMember.getId());
        if (request.standard() == Standard.PF) {
            newExam.updateHighestScore(-1);

        } else if (request.standard() == Standard.EVALUATION) {
            newExam.updateHighestScore(-2);
        }

        Exam savedExam = examRepository.save(newExam);


        int defaultScore;
        if (request.standard() == Standard.PF) {
            defaultScore = -3; // PF : -3(P) / -4(F)
        } else if (request.standard() == Standard.EVALUATION) {
            defaultScore = -5; // Evaluation : -5
        } else {
            defaultScore = 0; // 기본값은 0
        }

        List<Enrollment> enrollments = enrollmentRepository.findByAcademyIdAndSubClass(findMember.getAcademy().getId(), findSubClass);
        List<StudentExamScore> studentExamScores = enrollments.stream().map(classStudent -> {
            Student student = classStudent.getStudent();
            return new StudentExamScore(student, savedExam, defaultScore, null, null); // 초기 점수는 0
        }).collect(Collectors.toList());
        studentExamScoreRepository.saveAll(studentExamScores);

        return CreateExamResponse.from(savedExam);
    }

    @Transactional(readOnly = true)
    public List<ExamClassStudent> findExamClassStudent(@AuthMember Member findMember, Long examId) {
        Exam findExam = examRepository.findById(examId)
                .orElseThrow(() -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findMember.getAcademy().getId());

        List<StudentExamScore> studentExamScores = studentExamScoreRepository.findByAcademyIdAndExam(findMember.getAcademy().getId(), findExam);

        return studentExamScores.stream().map(studentExamScore -> {
            Student student = studentExamScore.getStudent();
            Integer score = studentExamScore.getScore();
            String evaluationDetail = studentExamScore.getEvaluationDetail();
            boolean checkedStudent = studentExamScore.isCheckedStudent();
            LocalDateTime updateAt = studentExamScore.getUpdatedAt();

            return ExamClassStudent.of(student.getId(), student.getName(), score,
                    findExam.getHighestScore(), evaluationDetail, checkedStudent, updateAt);
        }).collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<FindExamResponse> findExamList(@AuthMember Member findMember, FindExamRequest request) {

        validateAcademy(findMember, findMember.getAcademy().getId());

        List<Exam> exams = null;

        if (request.memberName() == null && request.examName() == null
                && request.mainClassId() == null && request.subClassId() == null) {
            exams = examRepository.findAllByAcademyId(findMember.getAcademy().getId());
        } else if (request.mainClassId() != null && request.subClassId() != null) {
            exams = examRepository.findByAcademyIdAndMainClassIdAndSubClassId(findMember.getAcademy().getId(), request.mainClassId(),
                    request.subClassId());
        } else if (request.memberName() != null && request.examName() == null) {
            exams = examRepository.findByAcademyIdAndMemberName(findMember.getAcademy().getId(),
                    request.memberName());
        } else if (request.memberName() == null && request.examName() != null) {
            exams = examRepository.findByAcademyIdAndExamName(findMember.getAcademy().getId(), request.examName());
        } else {
            throw new ClassfitException(ErrorCode.SEARCH_NOT_AVAILABLE);
        }

        if (exams == null || exams.isEmpty()) {
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
        List<StudentExamScore> studentScores = findExam.getStudentExamScores();
        List<Enrollment> enrollments = enrollmentRepository.findByAcademyIdAndSubClass(findMember.getAcademy().getId(), subClass);
        Integer perfectScore = studentScores.stream().mapToInt(StudentExamScore::getScore).max()
                .orElse(findExam.getHighestScore());
        Integer lowestScore = studentScores.stream().mapToInt(StudentExamScore::getScore).min()
                .orElse(0);
        Double average = (Double) studentScores.stream().mapToInt(StudentExamScore::getScore)
                .average()
                .orElse((perfectScore + lowestScore) / 2.0);
        String formattedAverage = String.format("%.1f", average);
        findExam.updateScores(lowestScore, perfectScore, average);

        examRepository.save(findExam);

        List<ExamClassStudent> examClassStudents = enrollments.stream().map(classStudent -> {
                    Student student = classStudent.getStudent();

                    Integer score = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId().equals(student.getId()))
                            .map(StudentExamScore::getScore).findFirst().orElse(0);

                    String evaluationDetail = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(findMember.getAcademy().getId(), findExam,
                                    student.getId())
                            .map(StudentExamScore::getEvaluationDetail)
                            .orElse(null);
                    boolean checkedStudent = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId().equals(student.getId()))
                            .map(StudentExamScore::isCheckedStudent)
                            .findFirst()
                            .orElse(false);

                    LocalDateTime updateAt = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId().equals(student.getId()))
                            .map(StudentExamScore::getUpdatedAt)
                            .findFirst()
                            .orElse(LocalDateTime.now());

                    return new ExamClassStudent(student.getId(), student.getName(), score, evaluationDetail,
                            checkedStudent, updateAt);
                })

                .collect(Collectors.toList());

        List<String> examRangeList = Arrays.asList(findExam.getExamRange().split(","));
        return new ShowExamDetailResponse(findExam.getExamPeriod(), findExam.getExamName(),
                findExam.getExamDate(), findExam.getMainClass().getMainClassName(),
                findExam.getSubClass().getSubClassName(), lowestScore, perfectScore,
                formattedAverage,
                findExam.getHighestScore(),
                examRangeList, findExam.getStandard(), examClassStudents);
    }

    @Transactional
    public UpdateExamResponse updateExam(@AuthMember Member findMember, Long examId,
                                         UpdateExamRequest request) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findExam.getMainClass().getAcademy().getId());

        Integer newHighestScore = request.highestScore();

        List<StudentExamScore> studentExamScores = studentExamScoreRepository.findAllByAcademyIdAndExam(findMember.getAcademy().getId(),
                findExam);

        if (newHighestScore != null && newHighestScore > 0) {
            for (StudentExamScore studentExamScore : studentExamScores) {
                if (studentExamScore.getScore() > newHighestScore) {
                    studentExamScore.updateScore(0);
                    studentExamScoreRepository.save(studentExamScore);
                }
            }
        }

        List<String> examRangeList = request.examRange();
        findExam.updateExam(request.examDate(), request.standard(), newHighestScore,
                request.examPeriod(), request.examName(), examRangeList);
        examRepository.save(findExam);

        return new UpdateExamResponse(findExam.getId(), findExam.getMainClass().getMainClassName(),
                findExam.getSubClass().getSubClassName(), findExam.getExamDate(),
                findExam.getStandard(), findExam.getHighestScore(), findExam.getExamPeriod(),
                findExam.getExamName(), examRangeList);
    }


    @Transactional
    public void deleteExam(@AuthMember Member findMember, Long examId) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findExam.getMainClass().getAcademy().getId());
        examRepository.delete(findExam);
    }

    @Transactional
    public UpdateStudentScoreResponse updateStudentScore(@AuthMember Member findMember, Long examId,
                                                         List<UpdateStudentScoreRequest> requests) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findExam.getMainClass().getAcademy().getId());

        for (UpdateStudentScoreRequest request : requests) {
            StudentExamScore studentExamScore = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(findMember.getAcademy().getId(),
                    findExam, request.studentId()).orElseGet(() -> {
                Student student = studentRepository.findById(request.studentId()).orElseThrow(
                        () -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));
                StudentExamScore newScore = StudentExamScore.create(findExam, student,
                        request.score());
                return studentExamScoreRepository.save(newScore);
            });

            studentExamScore.updateScore(request.score());

            if (findExam.getHighestScore() == -1) { // PF
                if (request.score() == -3) {  // P
                    studentExamScore.updateScore(-3);
                } else if (request.score() == -4) {  // F
                    studentExamScore.updateScore(-4);
                }
            } else if (findExam.getHighestScore() == -2) { // Evaluation
                studentExamScore.updateScore(-5);  // Evaluation -5
                studentExamScore.updateEvaluationDetail(request.evaluationDetail());
            }

            // 체크 상태 업데이트
            studentExamScore.updateCheckedStudent(request.checkedStudent());
            studentExamScoreRepository.save(studentExamScore);
        }

        studentExamScoreRepository.flush();
        Standard standard = findExam.getStandard();

        List<Enrollment> enrollments = enrollmentRepository.findByAcademyIdAndSubClass(findMember.getAcademy().getId(),
                findExam.getSubClass());

        List<ExamStudent> examStudents = enrollments.stream().map(classStudent -> {
            Student student = classStudent.getStudent();
            Integer score = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(findMember.getAcademy().getId(), findExam,
                            student.getId())
                    .map(StudentExamScore::getScore)
                    .orElse(0);
            String evaluationDetail = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(findMember.getAcademy().getId(), findExam,
                            student.getId())
                    .map(StudentExamScore::getEvaluationDetail)
                    .orElse(null);

            boolean checkedStudent = requests.stream()
                    .filter(request -> request.studentId().equals(student.getId()))
                    .map(UpdateStudentScoreRequest::checkedStudent)
                    .findFirst()
                    .orElse(false);

            return ExamStudent.of(student.getId(), student.getName(), score,
                    findExam.getHighestScore(), evaluationDetail, checkedStudent);
        }).collect(Collectors.toList());

        return new UpdateStudentScoreResponse(standard, findExam.getHighestScore(), examStudents);
    }

}
