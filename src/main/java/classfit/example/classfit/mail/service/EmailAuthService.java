package classfit.example.classfit.mail.service;

import classfit.example.classfit.auth.security.jwt.JWTUtil;
import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.EmailUtil;
import classfit.example.classfit.common.util.RedisUtil;
import classfit.example.classfit.mail.dto.request.EmailAuthPurpose;
import classfit.example.classfit.mail.dto.request.EmailAuthRequest;
import classfit.example.classfit.mail.dto.request.EmailAuthVerifyRequest;
import classfit.example.classfit.mail.dto.response.EmailAuthResponse;
import classfit.example.classfit.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;
    private final JWTUtil jwtUtil;

    @Transactional
    public EmailAuthResponse sendEmail(EmailAuthRequest request) {

        boolean existsByEmail = memberRepository.existsByEmail(request.email());

        if (existsByEmail && request.purpose().equals(EmailAuthPurpose.SIGN_IN)) {
            throw new ClassfitException("이미 가입된 이메일입니다.", HttpStatus.CONFLICT);
        }

        if (!existsByEmail && request.purpose().equals(EmailAuthPurpose.PASSWORD_RESET)) {
            throw new ClassfitException("존재하지 않는 계정입니다.", HttpStatus.NOT_FOUND);
        }

        String email = createEmailForm(request.email());
        return EmailAuthResponse.of(email);
    }

    @Transactional
    public EmailAuthResponse verifyAuthCode(EmailAuthVerifyRequest request) {
        String authCode = redisUtil.getData("email_code:" + request.email());

        if (authCode == null) {
            throw new ClassfitException("인증 코드가 만료되었거나 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        if (!authCode.equals(request.code())) {
            throw new ClassfitException("이메일 인증 번호가 일치하지 않습니다.", HttpStatus.NOT_FOUND);
        }

        String emailJwt = jwtUtil.createEmailJwt("email", 60 * 5L);
        redisUtil.setDataExpire(request.purpose() + ":" + EmailUtil.splitEmail(request.email()) + ":token",
            emailJwt, 60 * 5L);
        return EmailAuthResponse.from(request.email(), emailJwt);
    }

    private String createEmailForm(String email) {

        String authCode = EmailUtil.createdCode();

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[ClassFit] 인증코드 확인");
            message.setText(setContext(authCode), "utf-8", "html");

            javaMailSender.send(message);

        } catch (MessagingException e) {
            throw new ClassfitException("이메일 전송에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        redisUtil.setDataExpire("email_code:" + email, authCode, 60 * 5L);
        return email;
    }

    private String setContext(String authCode) {
        Context context = new Context();
        context.setVariable("code", authCode);
        return templateEngine.process("mail", context);
    }
}
