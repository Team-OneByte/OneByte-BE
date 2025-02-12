package classfit.example.classfit.drive.controller;

import classfit.example.classfit.common.annotation.AuthMember;
import classfit.example.classfit.common.response.CustomApiResponse;
import classfit.example.classfit.drive.controller.docs.DriveTrashControllerDocs;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DriveFileResponse;
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
    @PostMapping("/trash")
    public CustomApiResponse<Integer> storeTrash(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam List<String> objectNames
    ) {
        Integer updateObject = driveTrashService.storeTrash(member, driveType, objectNames);
        return CustomApiResponse.success(updateObject, 200, "휴지통 이동 성공");
    }

    @Override
    @PostMapping("/restore")
    public CustomApiResponse<Integer> restoreTrash(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam List<String> objectNames
    ) {
        Integer restorePathList = driveRestoreService.restoreTrash(member, driveType, objectNames);
        return CustomApiResponse.success(restorePathList, 200, "휴지통 복원 성공");
    }

    @Override
    @DeleteMapping("/trash")
    public CustomApiResponse<Void> deleteFromTrash(
            @AuthMember Member member,
            @RequestParam DriveType driveType,
            @RequestParam List<String> objectNames
    ) {
        driveDeleteService.deleteFromTrash(member, driveType, objectNames);
        return CustomApiResponse.success(null, 200, "휴지통 영구삭제 성공");
    }

    @Override
    @GetMapping("/trash")
    public CustomApiResponse<List<DriveFileResponse>> getTrashList(
            @AuthMember Member member,
            @RequestParam DriveType driveType
    ) {
        List<DriveFileResponse> filesFromTrash = driveTrashService.getTrashList(member, driveType);
        return CustomApiResponse.success(filesFromTrash, 200, "휴지통 조회 성공");
    }
}
