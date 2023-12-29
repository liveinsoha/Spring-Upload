package hello.upload.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@Slf4j
@RequestMapping("/spring")
public class SpringUploadController {

    @Value("${file.dir}")
    private String filePath;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    /**
     * value없이 쓰기 위해 파라미터명과 name을 맞춰줘야 한다
     * `@RequestParam MultipartFile file`
     * 업로드하는 HTML Form의 name에 맞추어 `@RequestParam` 을 적용하면 된다.
     * 추가로 `@ModelAttribute` 에서도 `MultipartFile` 을 동일하게 사용할 수 있다.
     * ArgumentResolver가 다 처리해준다!
     */
    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName, @RequestParam MultipartFile file, HttpServletRequest request) throws IOException {

        log.info("request = {}", request);
        log.info("itemName = {}", itemName);
        log.info("file = {}", file);

        if (!file.isEmpty()) {
            String fullPath = filePath + file.getOriginalFilename();
            log.info("File Save : {}", fullPath);
            file.transferTo(new File(fullPath));
        }

        return "upload-form";
    }
}
