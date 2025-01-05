package classfit.example.classfit.mail.service;

import classfit.example.classfit.auth.security.jwt.JWTUtil;
import classfit.example.classfit.common.exception.ClassfitException;
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
import org.springframework.http.HttpStatus;
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
            .orElseThrow(() -> new ClassfitException("지원하지 않는 이메일 사용 수단입니다.", HttpStatus.NOT_FOUND));

        handler.validate(email);
        String authCode = EmailUtil.createdCode();
        sendEmailForm(email, handler.getTitle(), handler.getTemplateVariables(authCode));

        if (handler.getExpirationTime() > 0) {
            redisUtil.setDataExpire(
                "email_code:" + purpose + ":" + email,
                authCode,
                handler.getExpirationTime()
            );
        }

        return EmailResponse.of(email);

    }

    @Transactional
    public EmailResponse verifyAuthCode(EmailVerifyRequest request) {
        String authCode = redisUtil.getData("email_code:" + request.purpose() + ":" + request.email());

        if (authCode == null) {
            throw new ClassfitException("인증 코드가 만료되었거나 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        if (!authCode.equals(request.code())) {
            throw new ClassfitException("이메일 인증 번호가 일치하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        String emailJwt = jwtUtil.createEmailJwt("email", 60 * 5L);

        redisUtil.setDataExpire("email_code:" + request.purpose() + ":" + request.email(), emailJwt, 60 * 5L);
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
            throw new ClassfitException("이메일 전송에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
