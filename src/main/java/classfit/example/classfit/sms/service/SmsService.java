package classfit.example.classfit.sms.service;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.response.ErrorCode;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.sms.dto.SendRequest;
import classfit.example.classfit.student.domain.Student;
import classfit.example.classfit.student.repository.StudentRepository;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SmsService {

    private final DefaultMessageService messageService;
    private final StudentRepository studentRepository;

    public SmsService(
        @Value("${coolsms.api-key}") String apiKey,
        @Value("${coolsms.api-secret}") String apiSecretKey,
        @Value("${coolsms.api-base-url}") String apiBaseUrl,
        StudentRepository studentRepository
    ) {
        this.messageService = new DefaultMessageService(apiKey, apiSecretKey, apiBaseUrl);
        this.studentRepository = studentRepository;
    }

    public MultipleDetailMessageSentResponse sendMessages(List<SendRequest> requestList, Member member) {

        String senderPhoneNumber = "01049259272";

        ArrayList<Message> messageList = new ArrayList<>();

        for (SendRequest request : requestList) {
            Student student = studentRepository.findById(Long.valueOf(request.studentId()))
                .orElseThrow(
                    () -> new ClassfitException(ErrorCode.STUDENT_NOT_FOUND));

            Message message = new Message();
            message.setFrom(senderPhoneNumber);
            message.setTo(student.getStudentNumber());
            message.setText(request.messageText());

            messageList.add(message);
        }

        try {
            return this.messageService.send(messageList, false, true);
        } catch (NurigoMessageNotReceivedException exception) {
            System.out.println("메세지 전송 실패: " + exception.getFailedMessageList());
            System.out.println("Error: " + exception.getMessage());
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }
        return null;
    }
}