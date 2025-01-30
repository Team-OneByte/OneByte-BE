package classfit.example.classfit.mail.service;

import classfit.example.classfit.auth.security.jwt.JWTUtil;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.common.util.EmailUtil;
import classfit.example.classfit.common.util.RedisUtil;
import classfit.example.classfit.mail.dto.request.EmailPurpose;
import classfit.example.classfit.mail.dto.request.EmailVerifyRequest;
import classfit.example.classfit.mail.dto.response.EmailResponse;
import classfit.example.classfit.mail.handler.EmailHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
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
        sendEmailForm(email, handler.getTitle(), handler.getTemplateVariables(authCode));

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

    private void sendEmailForm(String email, String subject, Map<String, Object> templateVariables) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject(subject);

            Context context = new Context();
            context.setVariables(templateVariables);

            String processedTemplate = templateEngine.process("mail", context);
            message.setText(processedTemplate, "utf-8", "html");

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new ClassfitException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }
}
