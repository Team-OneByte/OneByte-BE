package classfit.example.classfit.drive.domain.enumType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

public enum ObjectType {
    FOLDER("folder"),
    DOCUMENT("pdf", "doc", "docx", "txt", "xls", "xlsx"),
    IMAGE("jpg", "jpeg", "png", "gif", "bmp", "tiff"),
    VIDEO("mp4", "avi", "mkv", "mov", "wmv", "flv"),
    AUDIO("mp3", "wav", "aac", "flac", "ogg"),
    ARCHIVE("zip", "tar", "gz", "rar", "7z"),
    OTHER("other"),
    UNKNOWN("");

    ObjectType(String... extensions) {
        List<String> extensionsList = List.of(extensions);
    }
}
