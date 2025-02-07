package classfit.example.classfit.drive.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.controller.docs.DriveFolderControllerDocs;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.service.DriveFolderService;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drive")
public class DriveFolderController implements DriveFolderControllerDocs {

    private final DriveFolderService driveFolderService;

    @Override
    @PostMapping("/folder")
    public CustomApiResponse<String> createFolder(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam String folderName,
            @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        String fullPath = driveFolderService.createFolder(member, driveType, folderName, folderPath);
        return CustomApiResponse.success(fullPath, 200, "폴더 생성 성공");
    }

    @Override
    @GetMapping("/folders")
    public CustomApiResponse<List<String>> getFolders(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<String> folders = driveFolderService.getFolders(member, driveType, folderPath);
        return CustomApiResponse.success(folders, 200, "폴더 목록 조회 성공");
    }
}
