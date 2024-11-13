package classfit.example.classfit.student.dto.request;

import classfit.example.classfit.common.Gender;
import classfit.example.classfit.domain.Student;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record StudentRequest
    (
        @NotBlank(message = "이름은 필수 항목입니다.")
        @Size(max = 30) String name,

        @NotBlank(message = "성별은 필수 항목입니다.") Gender gender,

        @NotBlank(message = "생년월일은 필수 항목입니다.")
        @Past LocalDate birth,

        @NotBlank(message = "학생 전화번호는 필수 항목입니다.")
        @Size(max = 14) @Pattern(regexp = "^[0-9\\-]+$") String studentNumber,

        @NotBlank(message = "학부모 전화번호는 필수 항목입니다.")
        @Size(max = 14) @Pattern(regexp = "^[0-9\\-]+$") String parentNumber,

        @NotBlank(message = "학년은 필수 항목입니다.")
        @Size(max = 10) String grade,

        @NotBlank(message = "클래스는 필수 항목입니다.")
        @Size(max = 30) List<Long> subClassList,

        @NotBlank(message = "주소 등록은 필수 항목입니다.")
        @Size(max = 30) String address,

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
            .grade(grade())
            .address(address())
            .isStudent(isStudent)
            .remark(remark())
            .counselingLog(counselingLog())
            .build();
    }
}
