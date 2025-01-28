package classfit.example.classfit.auth.controller.docs;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "회원 컨트롤러", description = "회원 관련 API입니다.")
public interface AuthControllerDocs {

    @Operation(summary = "토큰 재발급", description = "토큰 재발급하는 API입니다.", responses = {
        @ApiResponse(responseCode = "200")
    })
    ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "로그아웃", description = "로그아웃 API입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    })
    CustomApiResponse<String> logout(@AuthMember Member member);
}