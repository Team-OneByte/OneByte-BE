package classfit.example.classfit.studentExam.dto.process;

import classfit.example.classfit.common.exception.ClassfitException;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;

public record ExamClassStudent(Long studentId, String name,
                               @Min(value = 0, message = "점수는 0 이상이어야 합니다.") Integer score,String evaluationDetail) {

    public static ExamClassStudent of(Long studentId, String name, Integer score,
            Integer highestScore,String evaluationDetail) {
        if (score > highestScore) {
            throw new ClassfitException("점수는 최고 점수(" + highestScore + ")를 초과할 수 없습니다.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ExamClassStudent(studentId, name, score,evaluationDetail);
    }
}
