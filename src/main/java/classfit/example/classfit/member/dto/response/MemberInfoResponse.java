package classfit.example.classfit.member.dto.response;

import classfit.example.classfit.member.domain.Member;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MemberInfoResponse(
        String name,
        String phoneNumber,
        LocalDate birth,
        String email,
        String subject
) {
    public static MemberInfoResponse from(final Member member) {
        return MemberInfoResponse.builder()
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .birth(member.getBirthDate())
                .subject(member.getSubject())
                .email(member.getEmail())
                .build();
    }
}
