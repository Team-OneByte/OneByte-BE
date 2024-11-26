package classfit.example.classfit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.starttls.enable}")
    private boolean startTlsEnable;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private boolean auth;

    @Bean
    public JavaMailSender googleMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(host);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setPort(port);

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put(MAIL_SMTP_AUTH, auth);
        properties.put(MAIL_SMTP_STARTTLS_ENABLE, startTlsEnable);

        javaMailSender.setJavaMailProperties(properties);
        javaMailSender.setDefaultEncoding("UTF-8");

        return javaMailSender;
    }
}
