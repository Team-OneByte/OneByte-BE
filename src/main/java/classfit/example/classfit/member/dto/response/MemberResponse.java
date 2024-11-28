package classfit.example.classfit.member.dto.response;

import classfit.example.classfit.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record MemberResponse
    (
        Long id,
        String email
    ) {

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
            .id(member.getId())
            .email(member.getEmail())
            .build();
    }
}
