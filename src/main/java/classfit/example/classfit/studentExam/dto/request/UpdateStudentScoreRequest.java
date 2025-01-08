package classfit.example.classfit.studentExam.dto.request;

import classfit.example.classfit.common.exception.ClassfitException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

public record UpdateStudentScoreRequest(Long studentId, @NotNull(message = "점수는 필수 입력값입니다.") Integer score,String evaluationDetail,boolean checkedStudent) {


    public static UpdateStudentScoreRequest of(Long studentId, Integer score,
            Integer highestScore,String evaluationDetail,boolean checkedStudent) {
        if (score != -3 && score != -4 && score != -5) {
            throw new ClassfitException("점수는 -3, -4, -5만 허용됩니다. 그 외의 값은 유효하지 않습니다.",
                    HttpStatus.BAD_REQUEST);
        }
        if (score > highestScore) {
            throw new ClassfitException("점수는 최고 점수(" + highestScore + ")를 초과할 수 없습니다.",
                    HttpStatus.BAD_REQUEST);
        }
        return new UpdateStudentScoreRequest(studentId, score, evaluationDetail, checkedStudent);
    }
}
