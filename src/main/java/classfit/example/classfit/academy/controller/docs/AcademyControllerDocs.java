package classfit.example.classfit.academy.controller.docs;

import classfit.example.classfit.academy.dto.request.AcademyCreateRequest;
import classfit.example.classfit.academy.dto.request.AcademyJoinRequest;
import classfit.example.classfit.academy.dto.response.AcademyResponse;
import classfit.example.classfit.common.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "학원 컨트롤러", description = "학원 관련 API입니다.")
public interface AcademyControllerDocs {

    @Operation(summary = "학원 생성", description = "학원 생성 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "학원 생성이 완료되었습니다.")
    })
    CustomApiResponse<AcademyResponse> createAcademy(@RequestBody AcademyCreateRequest request);

    @Operation(summary = "학원 가입", description = "학원 코드로 등록된 학원에 가입하는 API 입니다.", responses = {
        @ApiResponse(responseCode = "200", description = "학원 생성이 완료되었습니다.")
    })
    CustomApiResponse<Void> joinAcademy(@RequestBody AcademyJoinRequest request);
}
