package classfit.example.classfit.student.dto.request;

import classfit.example.classfit.common.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record StudentUpdateRequest
    (
        @Size(max = 30) String name,

        Gender gender,

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
