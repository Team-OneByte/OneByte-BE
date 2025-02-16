package classfit.example.classfit.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.calendar.memberCalendar.service.MemberCalendarService;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.JWTUtil;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.dto.request.MemberPasswordRequest;
import classfit.example.classfit.member.dto.request.MemberRequest;
import classfit.example.classfit.member.dto.request.MemberUpdateInfoRequest;
import classfit.example.classfit.member.dto.response.AcademyMemberResponse;
import classfit.example.classfit.member.dto.response.MemberInfoResponse;
import classfit.example.classfit.member.dto.response.MemberResponse;
import classfit.example.classfit.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DisplayName("회원가입 및 회원 정보 테스트")
class MemberServiceTest {

    @Mock
    JWTUtil jwtUtil;

    @Mock
    MemberRepository memberRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    MemberCalendarService memberCalendarService;

    @InjectMocks
    MemberService memberService;

    MemberRequest memberRequest;
    Member member;
    Academy academy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        academy = Academy.builder()
                .id(1L)
                .name("아카데미")
                .code("code")
                .build();

        member = Member.builder()
                .id(1L)
                .name("유저")
                .academy(academy)
                .email("test@example.com")
                .build();

        memberRequest = new MemberRequest(
                "유저", "01012341234", "test@example.com",
                "password", "password", "token"
        );
    }

    @Test
    @DisplayName("사용자가 회원가입에 성공한다.")
    void 회원_가입() {
        // Given
        when(jwtUtil.getEmail(anyString())).thenReturn("test@example.com"); // 이메일 토큰 검증
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashedPassword"); // 비밀번호 암호화
        when(memberRepository.save(any(Member.class))).thenReturn(member); // 저장된 회원 반환

        // When
        MemberResponse response = memberService.signUp(memberRequest);

        // Then
        assertNotNull(response);
        assertEquals("test@example.com", response.email());
        verify(memberRepository, times(1)).save(any(Member.class)); // 저장 호출 확인
    }


    @Test
    @DisplayName("회원가입 시, 이메일 토큰 검증에 실패한다.")
    void 회원_가입_실패() {
        // Given
        when(jwtUtil.getEmail(anyString())).thenReturn("wrong@example.com"); // 잘못된 이메일 반환

        // When & Then
        ClassfitException exception = assertThrows(ClassfitException.class,
                () -> memberService.signUp(memberRequest));

        assertEquals(ErrorCode.EMAIL_TOKEN_INVALID, exception.getErrorCode());
        verify(memberRepository, never()).save(any(Member.class)); // 저장되지 않아야 함
    }

    @Test
    @DisplayName("비밀번호 업데이트에 성공한다.")
    void 비밀번호_업데이트_성공() {
        // Given
        when(jwtUtil.getEmail(anyString())).thenReturn("test@example.com");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(member));
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashedPassword");

        // When
        MemberPasswordRequest request = new MemberPasswordRequest(
                "test@example.com",
                "newPassword",
                "newPassword",
                "emailToken"
        );
        memberService.updatePassword(request);

        // Then
        assertEquals("hashedPassword", member.getPassword());
        verify(memberRepository, times(1)).findByEmail("test@example.com");
        verify(bCryptPasswordEncoder, times(1)).encode("newPassword");
    }

    @Test
    @DisplayName("이메일이 존재하지 않아 비밀번호 업데이트에 실패한다.")
    void 비밀번호_업데이트_실패() {
        // Given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        MemberPasswordRequest request = new MemberPasswordRequest(
                "test@example.com",
                "newPassword",
                "newPassword",
                "emailToken"
        );

        ClassfitException exception = assertThrows(ClassfitException.class,
                () -> memberService.updatePassword(request));
        assertEquals(ErrorCode.EMAIL_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원정보를 조회한다.")
    void 회원정보_조회_성공() {
        // When
        MemberInfoResponse response = memberService.myPage(member);

        // Then
        assertNotNull(response);
        assertEquals("test@example.com", response.email());
    }

    @Test
    @DisplayName("회원 정보를 업데이트 한다.")
    void 회원정보_수정_성공() {
        // Given
        MemberUpdateInfoRequest request = new MemberUpdateInfoRequest(
                LocalDate.of(1990, 1, 1),
                "newNickname");

        // When
        MemberInfoResponse response = memberService.updateMyPage(member, request);

        // Then
        assertNotNull(response);
        assertEquals("newNickname", response.subject());
    }

    @Test
    @DisplayName("특정 학원에 존재하는 회원을 조회한다.")
    void 학원에_속한_회원_조회() {
        // given
        List<Member> academyMembers = List.of(
                Member.builder().id(1L).email("member1@example.com").build(),
                Member.builder().id(2L).email("member2@example.com").build()
        );

        when(memberRepository.findByAcademyId(1L)).thenReturn(Optional.of(academyMembers));

        // when
        List<AcademyMemberResponse> responses = memberService.getMembersByLoggedInMemberAcademy(
                member);

        // then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("member1@example.com", responses.get(0).email());
        assertEquals("member2@example.com", responses.get(1).email());
    }

    @Test
    @DisplayName("학원에 속한 회원일 경우, 예외가 발생한다.")
    void 회원_학원_검증() {
        // given
        Member anonymousMember = Member.builder().id(1L).email("example@naver.com")
                .academy(null).build();
        // when
        ClassfitException exception = assertThrows(ClassfitException.class,
                () -> memberService.getMembersByLoggedInMemberAcademy(anonymousMember));

        // then
        assertEquals(ErrorCode.MEMBER_ACADEMY_INVALID, exception.getErrorCode());

    }
}