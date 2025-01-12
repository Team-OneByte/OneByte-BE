package classfit.example.classfit.drive.domain;

import java.util.List;

public enum FileType {
    FOLDER("folder"),
    DOCUMENT("pdf", "doc", "docx", "txt", "xls", "xlsx"),
    IMAGE("jpg", "jpeg", "png", "gif", "bmp", "tiff"),
    VIDEO("mp4", "avi", "mkv", "mov", "wmv", "flv"),
    AUDIO("mp3", "wav", "aac", "flac", "ogg"),
    ARCHIVE("zip", "tar", "gz", "rar", "7z"),
    OTHER("other"),
    UNKNOWN("");

    private final List<String> extensions;

    FileType(String... extensions) {
        this.extensions = List.of(extensions);
    }

    public boolean hasExtension(String extension) {
        return extensions.contains(extension.toLowerCase());
    }

    public static FileType getFileTypeByExtension(String extension) {
        for (FileType type : values()) {
            if (type.hasExtension(extension)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
