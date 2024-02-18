package com.app.findgwangmyeongserver.controller;

import com.app.findgwangmyeongserver.entity.MsgEntity;
import com.app.findgwangmyeongserver.service.FgmService;
import com.app.findgwangmyeongserver.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("trade")
@RequiredArgsConstructor
public class FgmController {

    private final FgmService fgmService;
    private final FileService fileService;

    @GetMapping(value="/master/file")
    public ResponseEntity<Resource> getMasterFile() {
        String fileName   = "master.json";
        String masterList = fgmService.getMasterList();

        return fileService.getDataFile("fgm", "", fileName, masterList);
    }

    @GetMapping(value="/lawd/file")
    public ResponseEntity<Resource> getLawdFile(
             @RequestParam(value = "masterCd", defaultValue = "41000") String masterCd
    ) {
        String fileName = "lawd.json";
        String lawdList = fgmService.getLawdList(masterCd);

        return fileService.getDataFile("fgm", masterCd, fileName, lawdList);
    }

    @GetMapping(value="/subway/{path}/file")
    public ResponseEntity<Resource> getSubwayFile(
             @PathVariable("path") String path
    ) {
        String fileName = "subway.json";
        String subwayList = fgmService.getSubwayList();

        return fileService.getDataFile(path, "", fileName, subwayList);
    }

    /**
     * 시군구 아파트 목록 조회 후 저장
     * @param masterCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart/code")
    public ResponseEntity<MsgEntity> saveApartCode(
            @RequestParam(value = "masterCd", defaultValue = "41000") String masterCd
    ) throws Exception {
        fgmService.saveApartCode(masterCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 아파트 주소,도로명 저장
     * @param masterCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart/code/address")
    public ResponseEntity<MsgEntity> saveApartCodeAddress(
            @RequestParam(value = "masterCd", defaultValue = "41000") String masterCd,
            @RequestParam(value = "lawdCd", required = false) String lawdCd,
            @RequestParam(value = "saveCd", required = false) String saveCd
    ) throws Exception {
        fgmService.saveApartAddress(masterCd, lawdCd, saveCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 아파트 우편번호 저장
     * @param masterCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart/code/zipcode")
    public ResponseEntity<MsgEntity> saveApartCodeZipCode(
            @RequestParam(value = "masterCd", defaultValue = "41000") String masterCd,
            @RequestParam(value = "lawdCd", required = false) String lawdCd
    ) throws Exception {
        fgmService.saveApartZipCode(masterCd, lawdCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 아파트 단지 파일 저장
     * @param masterCd - 시군구코드
     * @return
     * @throws Exception
     */
    @GetMapping(value="/apart/code/file")
    public ResponseEntity<MsgEntity> getApartCodeList(
            @RequestParam(value = "masterCd", defaultValue = "41000") String masterCd
    ) throws Exception {
        List<Map<String, Object>> fileList = fgmService.getApartCodeList(masterCd);
        fileService.makeDataFile(masterCd, fileList);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 해당 시/군/구 디렉토리의 아파트 json 파일을 읽어 APART_LIST 데이터 업로드
     * @param masterCd - 시/군/구코드
     * @return
     * @throws Exception
     */
    @GetMapping(value="/apart/code/upload")
    public ResponseEntity<MsgEntity> uploadApartList(
            @RequestParam(value = "masterCd", defaultValue = "41000") String masterCd
    ) throws Exception {
        List<String> dataList = fileService.getApartFileFromJson(masterCd);
        fgmService.saveApartList(masterCd, dataList);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", dataList.size()));
    }

    @GetMapping(value="/bible/file")
    public ResponseEntity<Resource> getBibleFile() {
        String fileName = "bible.json";
        String bibleList = fgmService.getBibleList();

        return fileService.getDataFile("bible", "", fileName, bibleList);
    }





}
