package classfit.example.classfit.drive.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.controller.docs.DriveGetControllerDocs;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.domain.FileType;
import classfit.example.classfit.drive.dto.response.FileResponse;
import classfit.example.classfit.drive.service.DriveGetService;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drive")
public class DriveGetController implements DriveGetControllerDocs {

    private final DriveGetService driveGetService;

    @Override
    @GetMapping("/files")
    public CustomApiResponse<List<FileResponse>> getFiles(
        @AuthMember Member member,
        @RequestParam DriveType driveType,
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<FileResponse> fileUrls = driveGetService.getFilesFromS3(member, driveType, folderPath);
        return CustomApiResponse.success(fileUrls, 200, "파일 조회 성공");
    }

    @Override
    @GetMapping("/search")
    public CustomApiResponse<List<FileResponse>> searchFilesByName(
        @AuthMember Member member,
        @RequestParam DriveType driveType,
        @RequestParam(required = false, defaultValue = "") String fileName,
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<FileResponse> files = driveGetService.searchFilesByName(member, driveType, fileName, folderPath);
        return CustomApiResponse.success(files, 200, "파일 이름 검색 성공");
    }

    @Override
    @GetMapping("/filter")
    public CustomApiResponse<List<FileResponse>> filterFilesByExtension(
        @AuthMember Member member,
        @RequestParam DriveType driveType,
        @RequestParam FileType fileType,
        @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<FileResponse> files = driveGetService.classifyFilesByType(member, driveType, fileType, folderPath);
        return CustomApiResponse.success(files, 200, "확장자 필터링 성공");
    }
}