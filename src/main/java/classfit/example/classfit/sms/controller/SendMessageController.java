package classfit.example.classfit.sms.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.sms.controller.docs.SendMessageControllerDocs;
import classfit.example.classfit.sms.dto.SendRequest;
import classfit.example.classfit.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SendMessageController implements SendMessageControllerDocs {

    private final SmsService smsService;

    @Override
    @PostMapping("api/v1/send-many")
    public MultipleDetailMessageSentResponse sendMany(
            @AuthMember Member member,
            @RequestBody List<SendRequest> requestList
    ) {
        return smsService.sendMessages(requestList, member);
    }

}
