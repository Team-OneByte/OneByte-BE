package classfit.example.classfit.studentExam.service;

import static classfit.example.classfit.studentExam.domain.QExamScore.examScore;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamRepository;
import classfit.example.classfit.studentExam.domain.ExamScore;
import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.domain.StandardStatus;
import classfit.example.classfit.studentExam.domain.StudentExamScoreRepository;
import classfit.example.classfit.studentExam.dto.examScoreRequest.CreateExamScoreRequest;
import classfit.example.classfit.studentExam.dto.examScoreRequest.UpdateExamScoreRequest;
import classfit.example.classfit.studentExam.dto.examScoreResponse.CreateExamScoreResponse;
import classfit.example.classfit.studentExam.dto.examScoreResponse.UpdateExamScoreResponse;
import classfit.example.classfit.studentExam.dto.process.ExamStudent;
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
    private final StudentRepository studentRepository;

    @Transactional
    public List<CreateExamScoreResponse> createExamScore(@AuthMember Member findMember,
            CreateExamScoreRequest request) {
        Exam findExam = examRepository.findById(request.examId())
                .orElseThrow(() -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));

        List<ClassStudent> classStudents = classStudentRepository.findByAcademyIdAndSubClass(
                findMember.getAcademy().getId(), findExam.getSubClass());
        List<ExamScore> examScores = classStudents.stream()
                .map(classStudent -> ExamScore.toEntity(classStudent.getStudent(), findExam))
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

    @Transactional
    public UpdateExamScoreResponse updateExamScore(@AuthMember Member findMember, Long examId,
            List<UpdateExamScoreRequest> requests) {
        Exam findExam = examRepository.findById(examId).orElseThrow(
                () -> new ClassfitException(ErrorCode.EXAM_NOT_FOUND));

        for (UpdateExamScoreRequest request : requests) {
            ExamScore examScore = studentExamScoreRepository.findByExamAndStudentIdAndAcademyId(
                    findMember.getAcademy().getId(),
                    findExam, request.studentId()).orElseGet(() -> {
                Student student = studentRepository.findById(request.studentId()).orElseThrow(
                        () -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));
                ExamScore newExamScore = ExamScore.toEntity(student,findExam);
                studentExamScoreRepository.save(newExamScore);
                return newExamScore;
            });

            examScore.updateScore(request.score());

            if (findExam.getStandard() == Standard.PF) {
                examScore.updateStandardStatus(request.standardStatus());
            }
            else if (findExam.getStandard() == Standard.EVALUATION) {
                examScore.updateEvaluationDetail(request.evaluationDetail());
            }

            examScore.updateCheckedStudent(request.checkedStudent());

            studentExamScoreRepository.save(examScore);
        }

        studentExamScoreRepository.flush();

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

            StandardStatus standardStatus = requests.stream()
                    .filter(request -> request.studentId().equals(student.getId()))
                    .map(UpdateExamScoreRequest::standardStatus)
                    .findFirst()
                    .orElse(null);

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
                    findExam.getHighestScore(),standardStatus, evaluationDetail, checkedStudent);
        }).collect(Collectors.toList());

        return new UpdateExamScoreResponse( findExam.getHighestScore(), examStudents);
    }

}
