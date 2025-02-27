package classfit.example.classfit.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import classfit.example.classfit.academy.domain.Academy;
import classfit.example.classfit.academy.dto.request.AcademyCreateRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.academy.repository.AcademyRepository;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("학원 생성 테스트")
class AcademyCreateServiceTest {

    @InjectMocks
    private AcademyService academyService;

    @Mock
    private AcademyRepository academyRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .id(1L)
                .name("홍길동")
                .role("ADMIN")
                .build();
    }

    @Test
    @DisplayName("학원을 생성한다.")
    void 학원_생성_성공_케이스() {
        // given
        AcademyCreateRequest request = new AcademyCreateRequest(
                "test@example.com",
                "Test Academy",
                "TEST1234"
        );

        Academy academy = request.toEntity();

        when(memberRepository.findByEmail(request.email())).thenReturn(Optional.of(member));
        when(academyRepository.existsByCode(request.code())).thenReturn(false);
        when(academyRepository.existsByName(request.name())).thenReturn(false);
        when(academyRepository.save(any(Academy.class))).thenReturn(academy);

        // when
        AcademyResponse response = academyService.createAcademy(request);

        // then
        assertNotNull(response);
        assertEquals("Test Academy", response.name());
    }

    @Test
    @DisplayName("중복된 학원 코드를 검증한다.")
    void 학원코드_중복_케이스() {
        // given
        AcademyCreateRequest request = new AcademyCreateRequest(
                "test@example.com",
                "Test Academy",
                "ACADEMY123"
        );

        when(academyRepository.existsByCode(request.code())).thenReturn(true);

        // when & then
        ClassfitException exception = assertThrows(ClassfitException.class, () -> {
            academyService.createAcademy(request);
        });

        assertEquals(ErrorCode.ACADEMY_CODE_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @DisplayName("중복된 학원명을 검증한다.")
    void 학원명_중복_케이스() {
        // given
        AcademyCreateRequest request = new AcademyCreateRequest(
                "test@example.com",
                "Test Academy",
                "ACADEMY123"
        );

        when(academyRepository.existsByCode(request.code())).thenReturn(false);
        when(academyRepository.existsByName(request.name())).thenReturn(true);

        // when & then
        ClassfitException exception = assertThrows(ClassfitException.class, () -> {
            academyService.createAcademy(request);
        });

        assertEquals(ErrorCode.ACADEMY_ALREADY_EXISTS, exception.getErrorCode());
    }
}