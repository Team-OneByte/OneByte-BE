package classfit.example.classfit.student.dto.request;

import classfit.example.classfit.common.Gender;
import classfit.example.classfit.common.validation.EnumValue;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record StudentUpdateRequest
    (
        @Size(max = 30) String name,

        @EnumValue(target = Gender.class, message = "존재하지 않는 성별입니다.", ignoreCase = true)
        String gender,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past LocalDate birth,

        @Size(max = 14) @Pattern(regexp = "^[0-9\\-]+$") String studentNumber,

        @Size(max = 14) @Pattern(regexp = "^[0-9\\-]+$") String parentNumber,

        @Size(max = 10) String grade,

        @Size(max = 30) List<Long> subClassList,

        @Size(max = 30) String address,

        Boolean isStudent,

        String remark,

        String counselingLog
    ) {

}
