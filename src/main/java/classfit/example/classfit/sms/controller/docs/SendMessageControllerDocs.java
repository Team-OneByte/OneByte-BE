package classfit.example.classfit.sms.controller.docs;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.member.domain.Member;
import classfit.example.classfit.sms.dto.SendRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "단체 문자 전송 컨트롤러", description = "단체 문자 전송 API입니다.")
public interface SendMessageControllerDocs {

    @Operation(summary = "단체 문자 전송", description = "학생과 학생 부모님께 문자를 전송하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "문자가 성공적으로 전송되었습니다.")
    })
    MultipleDetailMessageSentResponse sendMany(
        @AuthMember Member member,
        @RequestBody List<SendRequest> requestList
    );
}

