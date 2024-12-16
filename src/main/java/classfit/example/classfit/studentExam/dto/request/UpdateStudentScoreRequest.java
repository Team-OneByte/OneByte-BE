package classfit.example.classfit.studentExam.dto.request;

import classfit.example.classfit.common.exception.ClassfitException;
import org.springframework.http.HttpStatus;

public record UpdateStudentScoreRequest(Long studentId, Integer score) {

    public UpdateStudentScoreRequest {
        if (score == null || score < 0) {
            throw new ClassfitException("점수는 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public static UpdateStudentScoreRequest of(Long studentId, Integer score,
            Integer highestScore) {
        if (score > highestScore) {
            throw new ClassfitException("점수는 최고 점수(" + highestScore + ")를 초과할 수 없습니다.",
                    HttpStatus.BAD_REQUEST);
        }
        return new UpdateStudentScoreRequest(studentId, score);
    }
}
