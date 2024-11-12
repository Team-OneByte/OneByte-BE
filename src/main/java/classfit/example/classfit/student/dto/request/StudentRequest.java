package classfit.example.classfit.student.dto.request;

import classfit.example.classfit.common.Gender;
import classfit.example.classfit.domain.Student;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record StudentRequest
    (
        @NotNull
        @Size(max = 30)
        String name,

        @NotNull
        Gender gender,

        @NotNull
        @Past
        LocalDate birth,

        @NotNull
        @Size(max = 14)
        @Pattern(regexp = "^[0-9\\-]+$") // 숫자, '-' 기호 허용
        String studentNumber,

        @NotNull
        @Size(max = 14)
        @Pattern(regexp = "^[0-9\\-]+$") // 숫자, '-' 기호 허용
        String parentNumber,

        @NotNull
        @Size(max = 10)
        String mainClass,

        @NotNull
        @Size(max = 10)
        String subClass,

        @NotNull
        @Size(max = 30)
        String address,

        String remark,

        String counselingLog
    ) {

    public Student toEntity(Boolean isStudent) {
        return Student.builder()
            .name(name())
            .gender(gender())
            .birth(birth())
            .studentNumber(studentNumber())
            .parentNumber(parentNumber())
            .mainClass(mainClass())
            .subClass(subClass())
            .address(address())
            .isStudent(isStudent)
            .remark(remark())
            .counselingLog(counselingLog())
            .build();
    }
}
