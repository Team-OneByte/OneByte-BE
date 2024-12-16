package classfit.example.classfit.studentExam.dto.process;

import classfit.example.classfit.common.exception.ClassfitException;
import org.springframework.http.HttpStatus;

public record ExamClassStudent(Long studentId, String name, Integer score) {

    public ExamClassStudent {
        if (score < 0) {
            throw new ClassfitException("점수는 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public static ExamClassStudent of(Long studentId, String name, Integer score,
            Integer highestScore) {
        if (score > highestScore) {
            throw new ClassfitException("점수는 최고 점수(" + highestScore + ")를 초과할 수 없습니다.",
                    HttpStatus.BAD_REQUEST);
        }
        return new ExamClassStudent(studentId, name, score);
    }
}
