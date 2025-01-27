package classfit.example.classfit.member.dto.response;

import classfit.example.classfit.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AcademyMemberResponse(
    Long id,

    String name,

    String email
) {
    public static AcademyMemberResponse from(Member member) {
        return AcademyMemberResponse.builder()
            .id(member.getId())
            .name(member.getName())
            .email(member.getEmail())
            .build();
    }
}
