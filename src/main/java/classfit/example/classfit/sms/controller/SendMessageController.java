package classfit.example.classfit.sms.controller;

import classfit.example.classfit.sms.service.SmsService;
import classfit.example.classfit.sms.dto.SendRequest;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SendMessageController {

    private final SmsService smsService;

    @PostMapping("/send-many")
    public MultipleDetailMessageSentResponse sendMany(
            @RequestHeader(name = "member-no") Long memberId,
            @RequestBody List<SendRequest> requestList) {

        return smsService.sendMessages(requestList, memberId);
    }

}
