package classfit.example.classfit.mail.service;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.EmailUtil;
import classfit.example.classfit.common.util.JWTUtil;
import classfit.example.classfit.common.util.RedisUtil;
import classfit.example.classfit.mail.dto.request.EmailPurpose;
import classfit.example.classfit.mail.dto.request.EmailVerifyRequest;
import classfit.example.classfit.mail.dto.response.EmailResponse;
import classfit.example.classfit.mail.handler.EmailHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailAsyncService emailAsyncService;
    private final RedisUtil redisUtil;
    private final JWTUtil jwtUtil;
    private final List<EmailHandler> handlers;

    @Transactional
    public EmailResponse sendEmail(String email, EmailPurpose purpose) {
        EmailHandler handler = handlers.stream()
                .filter(h -> h.supports(purpose))
                .findFirst()
                .orElseThrow(() -> new ClassfitException(ErrorCode.EMAIL_TYPE_INVALID));

        handler.validate(email);
        String authCode = EmailUtil.createdCode();
        emailAsyncService.sendEmailForm(email, handler.getTitle(), handler.getTemplateVariables(authCode));

        redisUtil.setDataExpire("email:" + purpose + ":" + email, authCode, handler.getExpirationTime());
        return EmailResponse.of(email);
    }

    @Transactional
    public EmailResponse verifyAuthCode(EmailVerifyRequest request) {
        String authCode = redisUtil.getData("email:" + request.purpose() + ":" + request.email());

        if (!authCode.equals(request.code())) {
            throw new ClassfitException(ErrorCode.EMAIL_AUTH_CODE_INVALID);
        }

        String emailJwt = jwtUtil.createJwt("email", request.email(), null, 1000L * 60 * 10);
        return EmailResponse.from(request.email(), emailJwt);
    }
}
