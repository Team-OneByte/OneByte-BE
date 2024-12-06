package classfit.example.classfit.studentExam.service;

import classfit.example.classfit.category.domain.MainClass;
import classfit.example.classfit.category.domain.SubClass;
import classfit.example.classfit.category.repository.MainClassRespository;
import classfit.example.classfit.category.repository.SubClassRepository;
import classfit.example.classfit.classStudent.domain.ClassStudent;
import classfit.example.classfit.classStudent.repository.ClassStudentRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import classfit.example.classfit.studentExam.domain.Exam;
import classfit.example.classfit.studentExam.domain.ExamRepository;
import classfit.example.classfit.studentExam.domain.StudentExamScore;
import classfit.example.classfit.studentExam.dto.process.ExamClassStudent;
import classfit.example.classfit.studentExam.dto.request.CreateExamRequest;
import classfit.example.classfit.studentExam.dto.request.FindExamRequest;
import classfit.example.classfit.studentExam.dto.response.CreateExamResponse;
import classfit.example.classfit.studentExam.dto.response.FindExamResponse;
import classfit.example.classfit.studentExam.dto.response.ShowExamClassStudentResponse;
import classfit.example.classfit.studentExam.dto.response.ShowExamDetailResponse;
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
    private final MemberRepository memberRepository;
    private final MainClassRespository mainClassRespository;
    private final SubClassRepository subClassRepository;
    private final ClassStudentRepository classStudentRepository;

    @Transactional
    public CreateExamResponse createExam(Long memberId, CreateExamRequest request) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        SubClass findSubClass = subClassRepository.findById(request.subClassId()).orElseThrow(
                () -> new ClassfitException("서브 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        MainClass findMainClass = mainClassRespository.findById(request.mainClassId()).orElseThrow(
                () -> new ClassfitException("메인 클래스를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        // TODO 시험범위 리스트형식으로 변환하기
        Exam newExam = request.toEntity(findSubClass, findMainClass);
        Exam savedExam = examRepository.save(newExam);

        return CreateExamResponse.from(savedExam);
    }

    @Transactional(readOnly = true)
    public List<ShowExamClassStudentResponse> findExamClassStuent(Long memberId, Long examId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        Exam findExam = examRepository.findById(examId)
                .orElseThrow(() -> new ClassfitException("시험지를 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        SubClass subClass = findExam.getSubClass();

        List<ClassStudent> classStudents = classStudentRepository.findBySubClass(subClass);

        return classStudents.stream().map(cs -> ShowExamClassStudentResponse.from(cs.getStudent(),
                findExam.getHighestScore())).toList();
    }

    @Transactional(readOnly = true)
    public List<FindExamResponse> findExamList(Long memberId, FindExamRequest request) {

        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        if (request.memberName() == null && request.examName() == null) {
            Long academyId = memberRepository.findAcademyIdByMemberName(request.memberName());

            return examRepository.findAllByAcademyId(academyId)
                    .stream()
                    .map(FindExamResponse::from)
                    .collect(Collectors.toList());
        } else if (request.memberName() != null && request.examName() == null) {

            return examRepository.findByMainClassMemberName(request.memberName())
                    .stream()
                    .map(FindExamResponse::from)
                    .collect(Collectors.toList());
        } else if (request.memberName() == null && request.examName() != null) {

            return examRepository.findByExamName(request.examName())
                    .stream()
                    .map(FindExamResponse::from)
                    .collect(Collectors.toList());
        } else {
            throw new ClassfitException("검색을 할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(readOnly = true)
    /**TODO
     *  시험범위 - 리스트형식,제목,클래스(json파싱),시험날짜,최저점수,최고점수,평균,반애들이름,성적리스트
     *  최저점수,최고점수,평균 : 도메인에 추가 - 초기 null
     *  상세 시험 애들 성적 수정하면 반영되도록 -> 최저점수
     */
    public ShowExamDetailResponse showExamDetail(Long memberId, Long examId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ClassfitException("회원을 찾을 수 없어요.", HttpStatus.NOT_FOUND));
        Exam findExam = examRepository.findById(examId)
                .orElseThrow(
                        () -> new ClassfitException("해당 시험지를 찾을 수 없어요.", HttpStatus.NOT_FOUND));

        List<StudentExamScore> studentScores = findExam.getStudentExamScores();
        // 초기 상황 : 학생들 점수 입력 안했을 시
        // 초기 학생들 성적은 null
        Integer perfectScore = studentScores.stream().mapToInt(StudentExamScore::getScore).max()
                .orElse(findExam.getHighestScore());
        Integer lowestScore = studentScores.stream().mapToInt(StudentExamScore::getScore).min()
                .orElse(0);
        Long average = (long) studentScores.stream()
                .mapToInt(StudentExamScore::getScore)
                .average()
                .orElse((perfectScore + lowestScore) / 2);

        List<ExamClassStudent> examClassStudents = studentScores.stream()
                .map(studentScore -> new ExamClassStudent(
                        studentScore.getStudent().getId(),
                        studentScore.getStudent().getName(),
                        studentScore.getScore()
                ))
                .toList();

        return new ShowExamDetailResponse(
                findExam.getExamPeriod(),
                findExam.getExamName(),
                findExam.getExamDate(),
                findExam.getMainClass().getMainClassName(),
                findExam.getSubClass().getSubClassName(),
                lowestScore,
                perfectScore,
                average,
                findExam.getStandard(),
                examClassStudents
        );
    }

}
