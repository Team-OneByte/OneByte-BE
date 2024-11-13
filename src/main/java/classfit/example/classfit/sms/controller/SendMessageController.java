package classfit.example.classfit.sms.controller;

import classfit.example.classfit.sms.service.SmsService;
import classfit.example.classfit.sms.dto.SendRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "단체 문자 전송 컨트롤러", description = "단체 문자 전송 API입니다.")
public class SendMessageController {

    private final SmsService smsService;

    @PostMapping("api/v1/send-many")
    @Operation(summary = "단체 문자 전송", description = "학생Id 통해 전화번호 찾기,memberId로 멤버 전화번호 찾아서 발송 / 부모님 메세지 발송은 아직 작동되지 않습니다.")
    public MultipleDetailMessageSentResponse sendMany(
            @RequestHeader(name = "member-no") Long memberId,
            @RequestBody List<SendRequest> requestList) {

        return smsService.sendMessages(requestList, memberId);
    }

}
