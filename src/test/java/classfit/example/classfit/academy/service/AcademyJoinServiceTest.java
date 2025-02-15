package classfit.example.classfit.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.dto.request.AcademyJoinRequest;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.invitation.domain.Invitation;
import classfit.example.classfit.invitation.domain.enumType.InvitationType;
import classfit.example.classfit.invitation.repository.InvitationRepository;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("학원 가입 테스트")
class AcademyJoinServiceTest {

    @InjectMocks
    private AcademyService academyService;

    @Mock
    private AcademyRepository academyRepository;

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .id(1L)
                .name("홍길동")
                .role(null)
                .build();
    }

    @Test
    @DisplayName("사용자는 초대된 학원에 가입한다.")
    void 학원_합류_성공_케이스() {
        // given
        AcademyJoinRequest request = new AcademyJoinRequest("test@example.com", "TEST1234");

        Academy academy = Academy.builder()
                .id(1L)
                .name("Test Academy")
                .code("TEST1234")
                .build();

        Invitation invitation = Invitation.builder()
                .id(1L)
                .academy(academy)
                .status(InvitationType.IN_PROGRESS)
                .build();

        when(academyRepository.findByCode(request.code()))
                .thenReturn(Optional.of(academy));
        when(invitationRepository.findByAcademyIdAndEmail(academy.getId(), request.email()))
                .thenReturn(Optional.of(invitation));
        when(memberRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(member));

        // when
        academyService.joinAcademy(request);

        // then
        assertEquals(InvitationType.COMPLETED, invitation.getStatus());
        assertTrue(member.getRole().contains("MEMBER"));
    }

    @Test
    @DisplayName("초대 정보가 없으면 예외 발생")
    void 학원_합류_실패_케이스() {
        // given
        AcademyJoinRequest request = new AcademyJoinRequest("TEST1234", "test@example.com");

        Academy academy = Academy.builder()
                .id(1L)
                .build();

        when(academyRepository.findByCode(request.code()))
                .thenReturn(Optional.of(academy));

        when(invitationRepository.findByAcademyIdAndEmail(academy.getId(), request.email()))
                .thenReturn(Optional.empty());

        // when & then
        ClassfitException exception = assertThrows(ClassfitException.class, () -> {
            academyService.joinAcademy(request);
        });

        assertEquals(ErrorCode.ACADEMY_INVITATION_INVALID, exception.getErrorCode());
    }

}
