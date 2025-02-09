package classfit.example.classfit.drive.controller.docs;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "드라이브 폴더 컨트롤러", description = "드라이브 폴더 관련 API입니다.")
public interface DriveFolderControllerDocs {

    @Operation(summary = "폴더 생성", description = "새로운 폴더를 생성하는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "폴더 생성 성공")
    })
    CustomApiResponse<String> createFolder(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
            @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더에 생성됩니다.") @RequestParam(required = false, defaultValue = "") String folderPath,
            @Parameter(description = "생성할 폴더 이름입니다.") @RequestParam String folderName
            );

    @Operation(summary = "폴더 조회", description = "폴더들을 조회하는 API입니다.", responses = {
            @ApiResponse(responseCode = "200", description = "폴더 목록 조회 성공")
    })
    CustomApiResponse<List<String>> getFolders(
            @AuthMember Member member,
            @Parameter(description = "내 드라이브는 PERSONAL, 공유 드라이브는 SHARED 입니다.") @RequestParam DriveType driveType,
            @Parameter(description = "조회할 폴더 경로입니다. 비어 있으면 루트 폴더를 조회합니다.") @RequestParam(required = false, defaultValue = "") String folderPath
    );
}
