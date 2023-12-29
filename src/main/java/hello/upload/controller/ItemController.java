package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FileStore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.UriUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String addForm(@ModelAttribute ItemForm form) {
        return "item-form";
    }

    @PostMapping("/items/new")
    public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> imageFiles = fileStore.storeImageFiles(form.getImageFiles());

        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(imageFiles);
        itemRepository.save(item);
        /**
         * 보통 파일은 데이터베이스에 저장하지 않는다. 보통 STORAGE에 저장하고,
         * AWS를 쓰면 S3에 저장하고..
         * 데이터 베이스에 저장되는 객체인 UploadFile에는 실제 파일이 있는 것이 아니라 경로가 저장되어 있다
         * 경로도 보통은 fullPath를 저장하기 보단 어떤 틀에 대한 상대적인 경로들만 저장되어 있다.
         */
        redirectAttributes.addAttribute("itemId", item.getId());

        return "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{itemId}")
    public String items(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item", findItem);
        return "item-view";
    }


    /**
     *
     */
    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
        /**
         * UrlResource의 역할은 해당 경로에 있는 파일을 직접 찾아와서 스트림으로 반환한다.
         * file: 이 있어야 내부 파일에 접근한다
         */
    }


    /**
     * <a></a>를 클릭하면 웹브라우저는 경로만 링크로 주는 거지, 다운로드를 진행해주지는 않는다
     * 그 경로를 매핑해서 다운로드를 진행할 수 있도록 한다.
     */
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
        Item item = itemRepository.findById(itemId);
        UploadFile attachFile = item.getAttachFile();
        String storeFileName = attachFile.getStoreFileName();
        String uploadFileName = attachFile.getUploadFileName();

        Resource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
        log.info("uploadFileName = {}", uploadFileName);

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        //한글 같은 경우 꺠질 수 있기 때문에 인코딩된 파일명을 넣어준다.
        String contentDisposition = "attachment; filename=\"" +encodedUploadFileName + "\"";
        /**
         * 쌍따옴표로 감싸기 위함. 이 헤더를 추가해야 브라우저에서 파일을 열지 않고 다운로드 한다
         * 이 파일명으로 다운로드 한다
         */
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }


}
