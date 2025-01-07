package classfit.example.classfit.drive.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.service.DriveFolderService;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drive")
@Tag(name = "드라이브 폴더 컨트롤러", description = "드라이브 폴더 관련 API입니다.")
public class DriveFolderController {
    private final DriveFolderService driveFolderService;

    @PostMapping("/folder")
    @Operation(summary = "폴더 생성", description = "새로운 폴더를 생성하는 API입니다.")
    public ApiResponse<String> createFolder(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @Parameter(description = "생성할 폴더 이름입니다.")
        @RequestParam String folderName
    ) {
        String folderPath = driveFolderService.createFolder(member, driveType, folderName);
        return ApiResponse.success(folderPath, 200, "SUCCESS");
    }
}
