package hello.upload.file;

import hello.upload.domain.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {
    /**
     * MultipartFile 파일을 서버에 저장하는 역할을 담당한다.
     * `createStoreFileName()` : 서버 내부에서 관리하는 파일명은 유일한 이름을 생성하는 `UUID` 를 사용해서 충
     * 돌하지 않도록 한다.
     */

    @Value("${file.dir}")
    String fileDir;

    public List<UploadFile> storeImageFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFilesResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) { //비어 있지 않은 경우
                UploadFile uploadFile = storeFile(multipartFile);
                storeFilesResult.add(uploadFile);
            }
        }
        return storeFilesResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        /**
         * 스프링이 제공하는 MultipartFile을 받아 파일을 저장하고 우리가 만든 클래스인 UploadFile로 변환한다.
         */
        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));//파일 저장
        return new UploadFile(originalFilename, storeFileName);
    }

    public String getFullPath(String storeFileName) {
        return fileDir + storeFileName;
    }


    private String createStoreFileName(String originalFileName) {
        /**
         * 서버에 저장하는 파일명은 UUID를 사용한다.
         * 확장자는 파일 관리할 경우 편리하기 댸문에 추출하여 붙여준다.
         */
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFileName) {
        int index = originalFileName.lastIndexOf(".");
        String ext = originalFileName.substring(index + 1);
        return ext;
    }
}
