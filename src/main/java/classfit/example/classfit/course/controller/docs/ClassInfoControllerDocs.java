package classfit.example.classfit.course.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.course.dto.response.ClassInfoResponse;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "클래스 정보 조회 컨트롤러", description = "클래스 정보 조회 API입니다.")
public interface ClassInfoControllerDocs {

    @Operation(summary = "클래스 정보 조회", description = "클래스 정보 조회 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "클래스 정보 조회 성공")
    })
    CustomApiResponse<List<ClassInfoResponse>> getClassInfo(@AuthMember Member member);
}
