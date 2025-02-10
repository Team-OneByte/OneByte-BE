package classfit.example.classfit.drive.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.controller.docs.DriveGetControllerDocs;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.domain.enumType.ObjectType;
import classfit.example.classfit.drive.dto.response.DriveFileResponse;
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
    public CustomApiResponse<List<DriveFileResponse>> getObjectList(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<DriveFileResponse> fileUrls = driveGetService.getObjectList(member, driveType, folderPath);
        return CustomApiResponse.success(fileUrls, 200, "파일 조회 성공");
    }

    @Override
    @GetMapping("/search")
    public CustomApiResponse<List<DriveFileResponse>> searchFilesByName(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam(required = false, defaultValue = "") String fileName,
            @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<DriveFileResponse> files = driveGetService.searchFilesByName(member, driveType, fileName, folderPath);
        return CustomApiResponse.success(files, 200, "파일 이름 검색 성공");
    }

    @Override
    @GetMapping("/filter")
    public CustomApiResponse<List<DriveFileResponse>> filterFilesByExtension(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam String objectType,
            @RequestParam(required = false, defaultValue = "") String folderPath
    ) {
        List<DriveFileResponse> files = driveGetService.classifyFilesByType(member, driveType, objectType, folderPath);
        return CustomApiResponse.success(files, 200, "확장자 필터링 성공");
    }
}