package hello.upload.controller;

import hello.upload.domain.UploadFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ItemForm {
    /**
     * 컨트롤러와 뷰 사이를 왔다갔다 하는 폼
     * MultipartFile을 사용한다(스프링에서 제공)
     */

    private Long itemId;
    private String itemName;
    private List<MultipartFile> imageFiles;
    private MultipartFile attachFile;

}
