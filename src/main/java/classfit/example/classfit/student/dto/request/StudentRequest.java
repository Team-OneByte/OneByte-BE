package classfit.example.classfit.student.dto.request;

import classfit.example.classfit.common.Gender;
import classfit.example.classfit.common.validation.EnumValue;
import classfit.example.classfit.domain.Student;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record StudentRequest
    (
        @NotBlank(message = "이름은 필수 항목입니다.")
        @Size(max = 30) String name,

        @EnumValue(target = Gender.class, message = "존재하지 않는 성별입니다.", ignoreCase = true)
        String gender,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past LocalDate birth,

        @NotBlank(message = "학생 전화번호는 필수 항목입니다.")
        @Size(max = 14) @Pattern(regexp = "^[0-9\\-]+$") String studentNumber,

        @NotBlank(message = "학부모 전화번호는 필수 항목입니다.")
        @Size(max = 14) @Pattern(regexp = "^[0-9\\-]+$") String parentNumber,

        @NotBlank(message = "학년은 필수 항목입니다.")
        @Size(max = 10) String grade,

        @NotNull(message = "클래스는 필수 항목입니다.")
        @Size(max = 30) List<Long> subClassList,

        @NotBlank(message = "주소 등록은 필수 항목입니다.")
        @Size(max = 30) String address,

        String remark,

        String counselingLog
    ) {

    public Student toEntity(Boolean isStudent) {
        return Student.builder()
            .name(name())
            .gender(Gender.valueOf(gender().strip().toUpperCase()))
            .birth(birth())
            .studentNumber(studentNumber())
            .parentNumber(parentNumber())
            .grade(grade())
            .address(address())
            .isStudent(isStudent)
            .remark(remark())
            .counselingLog(counselingLog())
            .build();
    }
}
