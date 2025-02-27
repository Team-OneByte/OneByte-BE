package classfit.example.classfit.exam.repository;

import classfit.example.classfit.exam.domain.Exam;
import java.util.List;

public interface ExamRepositoryCustom {
    List<Exam> findExamsByConditions(Long academyId, Long mainClassId, Long subClassId, String memberName, String examName);
}
