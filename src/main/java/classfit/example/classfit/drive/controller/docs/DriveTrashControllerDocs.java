package classfit.example.classfit.drive.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DriveFileResponse;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "드라이브 휴지통 컨트롤러", description = "드라이브 휴지통 관련 API입니다.")
public interface DriveTrashControllerDocs {

    @Operation(summary = "휴지통 이동", description = "휴지통 이동 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "휴지통 이동 완료")
    })
    CustomApiResponse<Integer> storeTrash(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
            @Parameter(description = "파일 이름") @RequestParam List<String> objectNames
    );

    @Operation(summary = "휴지통 복원", description = "휴지통 복원 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "휴지통 복원 성공")
    })
    CustomApiResponse<Integer> restoreTrash(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
            @Parameter(description = "파일 이름") @RequestParam List<String> objectNames
    );

    @Operation(summary = "휴지통 조회", description = "휴지통 조회 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "휴지통 조회 성공")
    })
    CustomApiResponse<List<DriveFileResponse>> getTrashList(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType
    );

    @Operation(summary = "휴지통 영구삭제", description = "휴지통 영구삭제 API 입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "휴지통 영구삭제 성공")
    })
    CustomApiResponse<Void> deleteFromTrash(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
            @Parameter(description = "파일 이름") @RequestParam List<String> objectNames
    );
}
