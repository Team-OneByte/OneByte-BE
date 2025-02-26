package classfit.example.classfit.drive.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import classfit.example.classfit.drive.domain.enumType.DriveType;
import classfit.example.classfit.member.domain.Member;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;

@ExtendWith(MockitoExtension.class)
@DisplayName("드라이브 다운로드 테스트")
class DriveDownloadServiceTest {

    @InjectMocks
    private DriveDownloadService driveDownloadService;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private AmazonS3 amazonS3;

    @Mock
    private Member member;

    @Test
    @DisplayName("드라이브에 저장된 파일을 다운로드한다.")
    void 파일_다운로드_성공_케이스() throws IOException {
        // Given
        DriveType driveType = DriveType.PERSONAL;
        List<String> objectNames = List.of("file1.txt", "file2.txt");

        byte[] file1Bytes = "File1 Contents".getBytes();
        byte[] file2Bytes = "File2 Contents".getBytes();

        S3ObjectInputStream mockS3InputStream1 = new S3ObjectInputStream(
                new ByteArrayInputStream(file1Bytes), null);
        S3ObjectInputStream mockS3InputStream2 = new S3ObjectInputStream(
                new ByteArrayInputStream(file2Bytes), null);

        S3Object object1 = mock(S3Object.class);
        S3Object object2 = mock(S3Object.class);

        doReturn(mockS3InputStream1).when(object1).getObjectContent();
        doReturn(mockS3InputStream2).when(object2).getObjectContent();

        doReturn(object1, object2).when(amazonS3).getObject(any(GetObjectRequest.class));

        // When
        InputStreamResource result = driveDownloadService.downloadMultipleFiles(member, driveType,
                objectNames);

        // Then
        assertNotNull(result);
        byte[] zipData = IOUtils.toByteArray(result.getInputStream());
        assertTrue(zipData.length > 0);

        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipData));
        ZipEntry entry;
        List<String> extractedFiles = new ArrayList<>();

        while ((entry = zipInputStream.getNextEntry()) != null) {
            extractedFiles.add(entry.getName());
        }
        zipInputStream.close();

        assertEquals(2, extractedFiles.size());
        assertTrue(extractedFiles.contains("file1.txt"));
        assertTrue(extractedFiles.contains("file2.txt"));

        verify(amazonS3, times(2)).getObject(any(GetObjectRequest.class));
    }
}