package classfit.example.classfit.member.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MemberUpdateInfoRequest
    (
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Past LocalDate birth,

        @Size(max = 30)
        String subject
    ) {
}
