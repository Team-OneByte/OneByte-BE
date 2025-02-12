package classfit.example.classfit.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MainClassRequest(
        @NotBlank(message = "메인 클래스 이름은 비어 있을 수 없습니다.")
        @Size(max = 10, message = "메인 클래스 이름은 10자를 초과할 수 없습니다.")
        String mainClassName
) {
}
