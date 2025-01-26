package classfit.example.classfit.drive.controller;

import classfit.example.classfit.auth.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.controller.docs.DriveTrashControllerDocs;
import classfit.example.classfit.drive.domain.DriveType;
import classfit.example.classfit.drive.dto.response.FileResponse;
import classfit.example.classfit.drive.service.DriveDeleteService;
import classfit.example.classfit.drive.service.DriveRestoreService;
import classfit.example.classfit.drive.service.DriveTrashService;
import classfit.example.classfit.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drive")
public class DriveTrashController implements DriveTrashControllerDocs {

    private final DriveTrashService driveTrashService;
    private final DriveRestoreService driveRestoreService;
    private final DriveDeleteService driveDeleteService;

    @Override
    @GetMapping("/trash")
    public CustomApiResponse<List<FileResponse>> trashList(
        @AuthMember Member member,
        @RequestParam DriveType driveType
    ) {
        List<FileResponse> filesFromTrash = driveTrashService.getFilesFromTrash(member, driveType);
        return CustomApiResponse.success(filesFromTrash, 200, "휴지통 조회 성공");
    }

    @Override
    @PostMapping("/trash")
    public CustomApiResponse<List<String>> storeTrash(
        @AuthMember Member member,
        @RequestParam DriveType driveType,
        @RequestParam(required = false, defaultValue = "") String folderPath,
        @RequestParam List<String> fileNames
    ) {
        List<String> trashPathList = driveTrashService.storeTrash(member, driveType, folderPath, fileNames);
        return CustomApiResponse.success(trashPathList, 200, "휴지통 이동 성공");
    }

    @Override
    @PostMapping("/trash/restore")
    public CustomApiResponse<List<String>> restoreTrash(
        @AuthMember Member member,
        @RequestParam DriveType driveType,
        @RequestParam List<String> fileNames
    ) {
        List<String> restorePathList = driveRestoreService.restoreTrash(member, driveType, fileNames);
        return CustomApiResponse.success(restorePathList, 200, "휴지통 복원 성공");
    }

    @Override
    @DeleteMapping("/trash")
    public CustomApiResponse<Void> deleteFromTrash(
        @AuthMember Member member,
        @RequestParam DriveType driveType,
        @RequestParam(required = false, defaultValue = "") String folderPath,
        @RequestParam List<String> fileNames
    ) {
        driveDeleteService.deleteFromTrash(member, driveType, folderPath, fileNames);
        return CustomApiResponse.success(null, 200, "휴지통 영구삭제 성공");
    }
}
