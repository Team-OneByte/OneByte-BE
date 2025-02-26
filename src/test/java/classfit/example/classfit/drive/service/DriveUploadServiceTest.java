package classfit.example.classfit.drive.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import classfit.example.classfit.common.exception.ClassfitException;
import classfit.example.classfit.common.util.DriveUtil;
import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.drive.dto.response.DrivePreSignedResponse;
import classfit.example.classfit.drive.repository.DriveRepository;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("드라이브 업로드 테스트")
class DriveUploadServiceTest {

    @InjectMocks
    private DriveUploadService driveUploadService;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private DriveRepository driveRepository;

    private String bucketName;

    private Member member;

    @BeforeEach
    void setUp() throws MalformedURLException {
        MockitoAnnotations.openMocks(this);
        member = Member.builder().id(1L).build();
        bucketName = "test-bucket";
    }

    @Test
    @DisplayName("미리 서명된 URL을 발급한다")
    void URL_발급_성공_케이스() throws MalformedURLException {

        DriveType driveType = DriveType.PERSONAL;
        List<String> objectNames = List.of("image");

        String generatedObjectName = UUID.randomUUID() + "_image";
        String mockPresignedUrl = "https://s3.amazonaws.com/" + bucketName + "/"
                + DriveUtil.generatedOriginPath(member, driveType, generatedObjectName);

        when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenReturn(new URL(mockPresignedUrl));

        // when
        List<DrivePreSignedResponse> responses = driveUploadService.getPreSignedUrl(member,
                driveType, objectNames);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
    }


    @Test
    @DisplayName("파얼 업로드에 실패한다.")
    void 파일_업로드_실패_케이스() {
        // given
        DriveType driveType = DriveType.PERSONAL;
        List<String> objectNames = List.of("image");

        String objectKey = DriveUtil.generatedOriginPath(member, driveType, objectNames.get(0));

        // when & then
        assertThrows(ClassfitException.class, () ->
                driveUploadService.uploadConfirm(member, driveType, objectNames)
        );
    }
}