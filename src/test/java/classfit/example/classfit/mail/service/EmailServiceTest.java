package classfit.example.classfit.mail.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.JWTUtil;
import classfit.example.classfit.common.util.RedisUtil;
import classfit.example.classfit.mail.dto.request.EmailPurpose;
import classfit.example.classfit.mail.dto.request.EmailVerifyRequest;
import classfit.example.classfit.mail.dto.response.EmailResponse;
import classfit.example.classfit.mail.handler.EmailHandler;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private EmailAsyncService emailAsyncService;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private List<EmailHandler> handlers;

    @Mock
    private EmailHandler emailHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이메일 발송에 성공한다.")
    void 이메일_발송() {
        // Given
        String email = "test@example.com";
        EmailPurpose purpose = EmailPurpose.SIGN_UP;
        String authCode = "123456";
        int expirationTime = 600; // 예제 만료 시간

        when(handlers.stream()).thenReturn(Stream.of(emailHandler));
        when(emailHandler.supports(purpose)).thenReturn(true);
        when(emailHandler.getTitle()).thenReturn("Email Title");
        when(emailHandler.getTemplateVariables(authCode)).thenReturn(Map.of("code", authCode));
        when(emailHandler.getExpirationTime()).thenReturn((long) expirationTime);

        doNothing().when(emailAsyncService)
                .sendEmailForm(email, "Email Title", Map.of("code", authCode));
        doNothing().when(redisUtil).setDataExpire(anyString(), anyString(), anyInt());

        // When
        EmailResponse response = emailService.sendEmail(email, purpose);

        // Then
        assertNotNull(response);
        assertEquals(email, response.email());

        verify(emailAsyncService, times(1)).sendEmailForm(
                email, "Email Title", Map.of()
        );
    }

    @Test
    @DisplayName("이메일 인증 코드 검증한다.")
    void 이메일_인증_코드_검증_성공() {
        // given
        EmailVerifyRequest request = new EmailVerifyRequest(
                "test@example.com",
                "12345678",
                EmailPurpose.SIGN_UP
        );

        String redisKey = "email:" + EmailPurpose.SIGN_UP + ":test@example.com";
        String jwtToken = "mockJwtToken";
        when(redisUtil.getData(redisKey))
                .thenReturn("12345678");
        when(jwtUtil.createJwt(eq("email"), eq(request.email()), isNull(), anyLong()))
                .thenReturn(jwtToken);

        // when
        EmailResponse response = emailService.verifyAuthCode(request);

        // then
        assertNotNull(response);
        assertEquals("test@example.com", response.email());
        assertEquals(jwtToken, response.emailToken());

        verify(redisUtil, times(1)).getData(redisKey);
        verify(jwtUtil, times(1)).createJwt(eq("email"), eq(request.email()), isNull(), anyLong());
    }

    @Test
    @DisplayName("이메일 인증 코드 검증에 실패한다.")
    void 이메일_인증_코드_검증_실패() {
        // Given
        EmailVerifyRequest request = new EmailVerifyRequest(
                "test@example.com",
                "wrongCode",
                EmailPurpose.SIGN_UP);
        String redisKey = "email:" + EmailPurpose.SIGN_UP + ":test@example.com";

        when(redisUtil.getData(redisKey)).thenReturn("123456");

        // When & Then
        assertThrows(ClassfitException.class, () -> emailService.verifyAuthCode(request));

        verify(redisUtil, times(1)).getData(redisKey);
        verify(jwtUtil, never()).createJwt(any(), any(), any(), anyLong());
    }
}