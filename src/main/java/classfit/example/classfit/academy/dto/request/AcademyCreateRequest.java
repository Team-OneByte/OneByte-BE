package classfit.example.classfit.academy.dto.request;

import classfit.example.classfit.academy.domain.Academy;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AcademyCreateRequest
    (

        @NotBlank(message = "이메일 정보를 입력해 주세요")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "학원명은 필수 입력입니다.")
        String name,

        @NotBlank(message = "학원 코드는 필수 입력입니다.")
        String code

    ) {
    public Academy toEntity() {

        return Academy.builder()
            .name(name())
            .code(code())
            .build();
    }
}
