package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {

    private final String UploadFileName;
    private final String StoreFileName;

    public UploadFile(String uploadFileName, String storeFileName) {
        UploadFileName = uploadFileName;
        StoreFileName = storeFileName;
    }
}
