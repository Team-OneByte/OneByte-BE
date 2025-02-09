package classfit.example.classfit.studentExam.service;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamRepository;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import classfit.example.classfit.studentExam.domain.ExamScore;
import classfit.example.classfit.studentExam.domain.StudentExamScoreRepository;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import classfit.example.classfit.studentExam.dto.process.ExamStudent;
import classfit.example.classfit.studentExam.dto.examRequest.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.examRequest.FindExamRequest;
import classfit.example.classfit.studentExam.dto.examRequest.UpdateExamRequest;
import classfit.example.classfit.studentExam.dto.examScoreRequest.CreateExamScoreRequest;
import classfit.example.classfit.studentExam.dto.examScoreRequest.UpdateExamScoreRequest;
import classfit.example.classfit.studentExam.dto.examResponse.CreateExamResponse;
import classfit.example.classfit.studentExam.dto.examResponse.FindExamResponse;
import classfit.example.classfit.studentExam.dto.examResponse.ShowExamDetailResponse;
import classfit.example.classfit.studentExam.dto.examResponse.UpdateExamResponse;
import classfit.example.classfit.studentExam.dto.examScoreResponse.UpdateExamScoreResponse;
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
    private final ClassStudentRepository classStudentRepository;
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
    public List<ExamClassStudent> findExamClassStudent(@AuthMember Member findMember, Long examId) {
        Exam findExam = examRepository.findById(examId)
                .orElseThrow(() -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findMember.getAcademy().getId());

        List<ExamScore> examScores = studentExamScoreRepository.findByAcademyIdAndExam(
                findMember.getAcademy().getId(), findExam);

        return examScores.stream().map(studentExamScore -> {
            Student student = studentExamScore.getStudent();
            Integer score = studentExamScore.getScore();
            String evaluationDetail = studentExamScore.getEvaluationDetail();
            boolean checkedStudent = studentExamScore.isCheckedStudent();

            return ExamClassStudent.of(student.getId(), student.getName(), score,
                    findExam.getHighestScore(), evaluationDetail, checkedStudent);
        }).collect(Collectors.toList());
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
        List<ClassStudent> classStudents = classStudentRepository.findByAcademyIdAndSubClass(
                findMember.getAcademy().getId(), subClass);
        Integer perfectScore = studentScores.stream().mapToInt(ExamScore::getScore).max()
                .orElse(findExam.getHighestScore());
        Integer lowestScore = studentScores.stream().mapToInt(ExamScore::getScore).min()
                .orElse(0);
        Double average = (Double) studentScores.stream().mapToInt(ExamScore::getScore)
                .average()
                .orElse((perfectScore + lowestScore) / 2.0);
        String formattedAverage = String.format("%.1f", average);
        findExam.updateScores(lowestScore, perfectScore, average);

        examRepository.save(findExam);

        List<ExamClassStudent> examClassStudents = classStudents.stream().map(classStudent -> {
                    Student student = classStudent.getStudent();

                    Integer score = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId().equals(student.getId()))
                            .map(ExamScore::getScore).findFirst().orElse(0);

                    String evaluationDetail = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(
                                    findMember.getAcademy().getId(), findExam,
                                    student.getId())
                            .map(ExamScore::getEvaluationDetail)
                            .orElse(null);
                    boolean checkedStudent = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId().equals(student.getId()))
                            .map(ExamScore::isCheckedStudent)
                            .findFirst()
                            .orElse(false);

                    LocalDateTime updateAt = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId().equals(student.getId()))
                            .map(ExamScore::getUpdatedAt)
                            .findFirst()
                            .orElse(LocalDateTime.now());

                    return new ExamClassStudent(student.getId(), student.getName(), score, evaluationDetail,
                            checkedStudent);
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

        List<ExamScore> examScores = studentExamScoreRepository.findAllByAcademyIdAndExam(
                findMember.getAcademy().getId(),
                findExam);

        if (newHighestScore != null && newHighestScore > 0) {
            for (ExamScore examScore : examScores) {
                if (examScore.getScore() > newHighestScore) {
                    examScore.updateScore(0);
                    studentExamScoreRepository.save(examScore);
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
    public UpdateExamScoreResponse updateStudentScore(@AuthMember Member findMember, Long examId,
            List<UpdateExamScoreRequest> requests) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));
        validateAcademy(findMember, findExam.getMainClass().getAcademy().getId());

        for (UpdateExamScoreRequest request : requests) {
            ExamScore examScore = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(
                    findMember.getAcademy().getId(),
                    findExam, request.studentId()).orElseGet(() -> {
                Student student = studentRepository.findById(request.studentId()).orElseThrow(
                        () -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));
                ExamScore newScore = ExamScore.create(findExam, student,
                        request.score());
                return studentExamScoreRepository.save(newScore);
            });

            examScore.updateScore(request.score());

            if (findExam.getHighestScore() == -1) { // PF
                if (request.score() == -3) {  // P
                    examScore.updateScore(-3);
                } else if (request.score() == -4) {  // F
                    examScore.updateScore(-4);
                }
            } else if (findExam.getHighestScore() == -2) { // Evaluation
                examScore.updateScore(-5);  // Evaluation -5
                examScore.updateEvaluationDetail(request.evaluationDetail());
            }

            // 체크 상태 업데이트
            examScore.updateCheckedStudent(request.checkedStudent());
            studentExamScoreRepository.save(examScore);
        }

        studentExamScoreRepository.flush();
        Standard standard = findExam.getStandard();

        List<ClassStudent> classStudents = classStudentRepository.findByAcademyIdAndSubClass(
                findMember.getAcademy().getId(),
                findExam.getSubClass());

        List<ExamStudent> examStudents = classStudents.stream().map(classStudent -> {
            Student student = classStudent.getStudent();
            Integer score = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(
                            findMember.getAcademy().getId(), findExam,
                            student.getId())
                    .map(ExamScore::getScore)
                    .orElse(0);
            String evaluationDetail = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(
                            findMember.getAcademy().getId(), findExam,
                            student.getId())
                    .map(ExamScore::getEvaluationDetail)
                    .orElse(null);

            boolean checkedStudent = requests.stream()
                    .filter(request -> request.studentId().equals(student.getId()))
                    .map(UpdateExamScoreRequest::checkedStudent)
                    .findFirst()
                    .orElse(false);

            return ExamStudent.of(student.getId(), student.getName(), score,
                    findExam.getHighestScore(), evaluationDetail, checkedStudent);
        }).collect(Collectors.toList());

        return new UpdateExamScoreResponse(standard, findExam.getHighestScore(), examStudents);
    }

}
