package classfit.example.classfit.studentExam.service;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.repository.MainClassRepository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
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
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final MainClassRepository mainClassRepository;
    private final SubClassRepository subClassRepository;
    private final ClassStudentRepository classStudentRepository;
    private final StudentExamScoreRepository studentExamScoreRepository;

    @Transactional
    public CreateExamResponse createExam(@AuthMember Member findMember, CreateExamRequest request) {
        SubClass findSubClass = subClassRepository.findById(request.subClassId()).orElseThrow(
                () -> new ClassfitException("서브 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        MainClass findMainClass = mainClassRepository.findById(request.mainClassId()).orElseThrow(
                () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        Exam newExam = request.toEntity(findSubClass, findMainClass);
        Exam savedExam = examRepository.save(newExam);

        int defaultScore;
        if (request.standard() == Standard.PF) {
            defaultScore = -3; // PF : -3(P) / -4(F)
        } else if (request.standard() == Standard.EVALUATION) {
            defaultScore = -5; // Evaluation : -5
        } else {
            defaultScore = 0; // 기본값은 0
        }

        List<ClassStudent> classStudents = classStudentRepository.findBySubClass(findSubClass);
        List<StudentExamScore> studentExamScores = classStudents.stream().map(classStudent -> {
            Student student = classStudent.getStudent();
            return new StudentExamScore(student, savedExam, defaultScore, null); // 초기 점수는 0
        }).collect(Collectors.toList());
        studentExamScoreRepository.saveAll(studentExamScores);

        return CreateExamResponse.from(savedExam);
    }

    @Transactional(readOnly = true)
    public List<ExamClassStudent> findExamClassStudent(@AuthMember Member findMember, Long examId) {
        Exam findExam = examRepository.findById(examId)
                .orElseThrow(() -> new ClassfitException("시험지를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        List<StudentExamScore> studentExamScores = studentExamScoreRepository.findByExam(findExam);

        return studentExamScores.stream().map(studentExamScore -> {
            Student student = studentExamScore.getStudent();
            return new ExamClassStudent(student.getId(), student.getName(),
                    studentExamScore.getScore());
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FindExamResponse> findExamList(@AuthMember Member findMember,
            FindExamRequest request) {

        if (request.memberName() == null && request.examName() == null) {
            return examRepository.findAll().stream()
                    .map(FindExamResponse::from)
                    .collect(Collectors.toList());
        } else if (request.memberName() != null && request.examName() == null) {

            return examRepository.findByMainClassMemberName(request.memberName()).stream()
                    .map(FindExamResponse::from).collect(Collectors.toList());
        } else if (request.memberName() == null && request.examName() != null) {

            return examRepository.findByExamName(request.examName()).stream()
                    .map(FindExamResponse::from).collect(Collectors.toList());
        } else {
            throw new ClassfitException("검색을 할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ShowExamDetailResponse showExamDetail(@AuthMember Member findMember, Long examId) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException("해당 시험지를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        SubClass subClass = findExam.getSubClass();
        List<StudentExamScore> studentScores = findExam.getStudentExamScores();
        List<ClassStudent> classStudents = classStudentRepository.findBySubClass(subClass);
        Integer perfectScore = studentScores.stream().mapToInt(StudentExamScore::getScore).max()
                .orElse(findExam.getHighestScore());
        Integer lowestScore = studentScores.stream().mapToInt(StudentExamScore::getScore).min()
                .orElse(0);
        Long average = (long) studentScores.stream().mapToInt(StudentExamScore::getScore).average()
                .orElse((perfectScore + lowestScore) / 2);
        findExam.updateScores(lowestScore, perfectScore, average);

        examRepository.save(findExam);

        List<ExamClassStudent> examClassStudents = classStudents.stream().map(classStudent -> {
                    Student student = classStudent.getStudent();

                    Integer score = studentScores.stream()
                            .filter(scoreObj -> scoreObj.getStudent().getId().equals(student.getId()))
                            .map(StudentExamScore::getScore).findFirst().orElse(0);

                    return new ExamClassStudent(student.getId(), student.getName(), score);
                })

                .collect(Collectors.toList());

        List<String> examRangeList = Arrays.asList(findExam.getExamRange().split(","));
        return new ShowExamDetailResponse(findExam.getExamPeriod(), findExam.getExamName(),
                findExam.getExamDate(), findExam.getMainClass().getMainClassName(),
                findExam.getSubClass().getSubClassName(), lowestScore, perfectScore, average,
                findExam.getHighestScore(),
                examRangeList, findExam.getStandard(), examClassStudents);
    }

    @Transactional
    public UpdateExamResponse updateExam(@AuthMember Member findMember, Long examId,
            UpdateExamRequest request) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException("해당 시험지를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        List<String> examRangeList = request.examRange();

        findExam.updateExam(request.examDate(), request.standard(), request.highestScore(),
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
                () -> new ClassfitException("해당 시험지를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        examRepository.delete(findExam);
    }

    @Transactional
    public UpdateStudentScoreResponse updateStudentScore(@AuthMember Member findMember, Long examId,
            List<UpdateStudentScoreRequest> requests) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException("해당 시험지를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        StudentExamScore studentExamScore = null;

        for (UpdateStudentScoreRequest request : requests) {
            studentExamScore = studentExamScoreRepository.findByExamAndStudentId(
                    findExam, request.studentId()).orElseThrow(
                    () -> new ClassfitException("해당 학생의 점수를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
            studentExamScore.updateScore(request.score());

            if (findExam.getHighestScore() == -1) { // PF
                if (request.score() == -3) {  // P
                    studentExamScore.updateScore(-3);
                } else if (request.score() == -4) {  // F
                    studentExamScore.updateScore(-4);
                } else {
                    throw new ClassfitException("PF 상태에서 점수는 -3(P) 또는 -4(F)여야 합니다.",
                            HttpStatus.BAD_REQUEST);
                }
            } else if (findExam.getHighestScore() == -2) { // Evaluation
                studentExamScore.updateScore(-5);  // Evaluation -5 //TODO 정성평가 저장 column 만들어야함
            }
        }

        studentExamScoreRepository.flush();
        Standard standard = findExam.getStandard();

        List<ClassStudent> classStudents = classStudentRepository.findBySubClass(
                findExam.getSubClass());

        List<ExamStudent> examStudents = classStudents.stream().map(classStudent -> {
            Student student = classStudent.getStudent();
            Integer score = studentExamScoreRepository.findByExamAndStudentId(findExam,
                            student.getId())
                    .map(StudentExamScore::getScore)
                    .orElse(0);

            boolean checkedStudent = requests.stream()
                    .filter(request -> request.studentId().equals(student.getId()))
                    .map(UpdateStudentScoreRequest::checkedStudent)
                    .findFirst()
                    .orElse(false);

            return ExamStudent.of(student.getId(), student.getName(), score,
                    findExam.getHighestScore(), checkedStudent);
        }).collect(Collectors.toList());

        return new UpdateStudentScoreResponse(standard, findExam.getHighestScore(), examStudents);
    }

}
