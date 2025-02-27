package classfit.example.classfit.invitation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.invitation.domain.Invitation;
import classfit.example.classfit.invitation.dto.request.InvitationRequest;
import classfit.example.classfit.invitation.dto.response.InvitationResponse;
import classfit.example.classfit.invitation.repository.InvitationRepository;
import classfit.example.classfit.mail.dto.request.EmailPurpose;
import classfit.example.classfit.mail.service.EmailService;
import classfit.example.classfit.member.domain.Member;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("학원 초대 테스트")
class InvitationServiceTest {

    @InjectMocks
    InvitationService invitationService;

    @Mock
    InvitationRepository invitationRepository;

    @Mock
    EmailService emailService;

    @Mock
    Academy academy;

    @Mock
    Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = Member.builder().academy(academy).build();
    }

    @Test
    @DisplayName("학원 코드 조회에 성공한다.")
    void 학원_코드_조회_성공() {
        // Given
        when(academy.getCode()).thenReturn("ABC123");

        // When
        String code = invitationService.findAcademyCode(member);

        // Then
        assertNotNull(code);
        assertEquals("ABC123", code);
    }

    @Test
    @DisplayName("학원 코드 조회에 실패한다.")
    void 학원_코드_조회_실패() {
        // Given
        Member checkMember = Member.builder().academy(null).build();
        // When & Then
        assertThrows(ClassfitException.class, () -> invitationService.findAcademyCode(checkMember));
    }

    @Test
    @DisplayName("특정 직원에게 이메일 전송에 성공한다.")
    void 직원_초대() {
        // given
        InvitationRequest request = new InvitationRequest("홍길동", "test@example.com");
        when(academy.getId()).thenReturn(1L);
        when(invitationRepository.existsByAcademyIdAndEmail(1L, "test@example.com")).thenReturn(
                false);

        // when
        invitationService.inviteStaffByEmail(member, request);

        // then
        verify(invitationRepository, times(1)).save(any(Invitation.class));
        verify(emailService, times(1)).sendEmail(eq("test@example.com"),
                eq(EmailPurpose.INVITATION));
    }

    @Test
    @DisplayName("이미 초대한 직원에게 이메일을 전송하지 않는다.")
    void 이미_초대한_직원_초대() {
        // given
        InvitationRequest request = new InvitationRequest("홍길동", "test@example.com");
        when(academy.getId()).thenReturn(1L);
        when(invitationRepository.existsByAcademyIdAndEmail(1L, "test@example.com")).thenReturn(
                true);

        // when
        invitationService.inviteStaffByEmail(member, request);

        // then
        verify(invitationRepository, never()).save(any(Invitation.class));
        verify(emailService, times(1)).sendEmail(eq("test@example.com"),
                eq(EmailPurpose.INVITATION));
    }

    @Test
    @DisplayName("학원에서 초대한 직원의 목록을 조회한다.")
    void 학원에서_초대한_직원_목록_조회() {
        // given
        List<Invitation> invitations = List.of(
                Invitation.builder().email("test1@example.com").academy(academy).build(),
                Invitation.builder().email("test2@example.com").academy(academy).build());

        when(invitationRepository.findByAcademy(any(Academy.class))).thenReturn(invitations);

        // when
        List<InvitationResponse> responses = invitationService.staffInfoAll(member);

        // then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("test1@example.com", responses.get(0).email());
        assertEquals("test2@example.com", responses.get(1).email());
    }
}