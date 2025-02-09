package classfit.example.classfit.studentExam.repository;

import classfit.example.classfit.studentExam.domain.Exam;
import java.util.List;

public interface ExamRepositoryCustom {
    List<Exam> findExamsByConditions(Long academyId, Long mainClassId, Long subClassId, String memberName, String examName);
}
