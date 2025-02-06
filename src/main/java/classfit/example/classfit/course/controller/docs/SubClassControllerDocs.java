package classfit.example.classfit.course.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.course.dto.request.SubClassRequest;
import classfit.example.classfit.course.dto.response.SubClassResponse;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@Tag(name = "하위 클래스 컨트롤러", description = "하위 클래스 관련 API입니다.")
public interface SubClassControllerDocs {

    @Operation(summary = "하위 클래스 추가", description = "하위 클래스를 추가하는 API입니다.", responses = {
            @ApiResponse(responseCode = "201", description = "하위 클래스 생성 성공")
    })
    CustomApiResponse<SubClassResponse> createSubClass(
            @AuthMember Member findMember,
            @Valid @RequestBody SubClassRequest req
    );

    @Operation(summary = "하위 클래스 수정", description = "하위 클래스 이름을 수정하는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "하위 클래스 수정 성공")
    })
    CustomApiResponse<SubClassResponse> updateSubClass(
            @AuthMember Member findMember,
            @PathVariable(name = "subClassId") Long subClassId,
            @Valid @RequestBody SubClassRequest req
    );

    @Operation(summary = "하위 클래스 삭제", description = "하위 클래스를 삭제하는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "하위 클래스 삭제 성공")
    })
    CustomApiResponse<Void> deleteSubClass(
            @AuthMember Member findMember,
            @PathVariable(name = "subClassId") Long subClassId
    );
}
