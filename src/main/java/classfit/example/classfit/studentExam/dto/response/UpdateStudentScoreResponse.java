package classfit.example.classfit.studentExam.dto.response;

import classfit.example.classfit.studentExam.domain.Standard;
import classfit.example.classfit.studentExam.dto.process.ExamStudent;
import java.util.List;

public record UpdateStudentScoreResponse(
        Standard standard,
        Integer highestScore,
        List<ExamStudent> examStudents
) {

}
