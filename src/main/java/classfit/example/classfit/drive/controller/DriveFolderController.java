package classfit.example.classfit.drive.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.ApiResponse;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.service.DriveFolderService;
import classfit.example.classfit.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        @RequestParam String folderName,
        @Parameter(description = "폴더 경로입니다. 비어 있으면 루트 폴더에 생성됩니다.")
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        String fullPath = driveFolderService.createFolder(member, driveType, folderName, folderPath);
        return ApiResponse.success(fullPath, 200, "SUCCESS");
    }

    @GetMapping("/folders")
    @Operation(summary = "폴더 조회", description = "폴더들을 조회하는 API입니다.")
    public ApiResponse<List<String>> getFolders(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @Parameter(description = "조회할 폴더 경로입니다. 비어 있으면 루트 폴더를 조회합니다.")
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<String> folders = driveFolderService.getFolders(member, driveType, folderPath);
        return ApiResponse.success(folders, 200, "폴더 목록 조회 성공");
    }

    @DeleteMapping("/folder")
    @Operation(summary = "폴더 삭제", description = "폴더를 삭제하는 API입니다.")
    public ApiResponse<Nullable> deleteFolder(
        @AuthMember Member member,
        @Parameter(description = "내 드라이브는 PERSONAL, 공용 드라이브는 SHARED 입니다.")
        @RequestParam DriveType driveType,
        @Parameter(description = "삭제할 폴더 이름입니다.")
        @RequestParam String folderName
    ) {
        driveFolderService.deleteFolder(member, driveType, folderName);
        return ApiResponse.success(null, 200, "SUCCESS");
    }
}
