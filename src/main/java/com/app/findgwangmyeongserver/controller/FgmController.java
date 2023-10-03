package com.app.findgwangmyeongserver.controller;

import com.app.findgwangmyeongserver.entity.MsgEntity;
import com.app.findgwangmyeongserver.service.FgmService;
import com.app.findgwangmyeongserver.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("trade")
@RequiredArgsConstructor
public class FgmController {

    private final FgmService fgmService;
    private final FileService fileService;
    private final LocalDateTime now = LocalDateTime.now();

    @GetMapping(value="{lawdDir}/{type}/file")
    public ResponseEntity<Resource> getFileTradeData(
            @PathVariable("lawdDir") String lawdDir,
            @PathVariable("type") String type,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd,
            @RequestParam("year") String year,
            @RequestParam("month") String month
    ) {
        String fileName = year + "-" + month + ".json";
        String tradeInfo = fgmService.getTradeInfo(lawdCd, type, year, month);

        return fileService.getDataFile(lawdDir, lawdCd, type, fileName, tradeInfo);
    }

    @GetMapping(value="{type}/file/cond")
    public void getFileTradeDataYear(
            @PathVariable("type") String type,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd,
            @RequestParam("year") String year
    ) throws IOException {
        List<Map<String, Object>> fileList = new ArrayList<>();
        int monthValue = now.getMonthValue();

        for (int num = 1; num <= monthValue; num++) {
            Map<String, Object> fileInfo = new HashMap<>();

            String month = String.valueOf(num < 10 ? "0" + num : num);
            String fileName = year + "-" + month + ".json";
            String tradeInfo = fgmService.getTradeInfo(lawdCd, type, year, month);

            fileInfo.put("fileName" , fileName);
            fileInfo.put("tradeInfo", tradeInfo);
            fileList.add(fileInfo);
        }

        fileService.makeDataFile(lawdCd, type, fileList);
    }

    /**
     * 광명찾자_아파트 거래내역 최신화
     * @param type   - 거래타입(deal : 매매, rent : 전월세)
     * @param year   - 거래년도
     * @param month  - 거래월
     * @param lawdCd - 지역코드
     * @return result - 1 : 업데이트 내역 존재, 0 : 업데이트 내역 없음
     * @throws Exception
     */
    @PutMapping(value="/{type}/latest/{year}/{month}")
    public ResponseEntity<MsgEntity> saveLatestTradeData(
            @PathVariable("type") String type,
            @PathVariable("year") String year,
            @PathVariable("month") String month,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        int result = fgmService.saveLatestTradeData(lawdCd, type, year, month);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", result == 1 ? "Data has been updated" : "No data changed"));
    }

    /**
     * 광명찾자_아파트 거래내역 최신화
     * @param type   - 거래타입(deal : 매매, rent : 전월세)
     * @param year   - 거래년도
     * @param lawdCd - 지역코드
     * @return result - 1 : 업데이트 내역 존재, 0 : 업데이트 내역 없음
     * @throws Exception
     */
    @PutMapping(value="/{type}/latest/{year}")
    public ResponseEntity<MsgEntity> saveLatestTradeDataYear(
            @PathVariable("type") String type,
            @PathVariable("year") String year,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        boolean isUpdate = false;
        int monthValue = now.getMonthValue();

        for (int month = 1; month <= monthValue; month++) {
            int result = fgmService.saveLatestTradeData(lawdCd, type, year, String.valueOf(month < 10 ? "0" + month : month));

            if (!isUpdate && result > 1) isUpdate = true;
        }

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", isUpdate ? "Data has been updated" : "No data changed"));
    }

    @PostMapping(value="/geom")
    public ResponseEntity<MsgEntity> saveGeom() throws Exception {
        fgmService.saveGeom();

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    @GetMapping(value="{lawdDir}/geom/file")
    public ResponseEntity<Resource> getFileGeomDataToName(
            @PathVariable("lawdDir") String lawdDir,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd,
            @RequestParam("name") String name
    ) {
        String fileName = "geom.json";
        String tradeInfo = fgmService.geomInfo(name);

        return fileService.getDataFile(lawdDir, lawdCd, "", fileName, tradeInfo);
    }

    @GetMapping(value="{lawdDir}/file/lawd")
    public ResponseEntity<Resource> getLawdFile(
            @PathVariable("lawdDir") String lawdDir
    ) {
        String fileName = "lawd.json";
        String lawdInfo = fgmService.lawdList();

        return fileService.getDataFile(lawdDir, "", "", fileName, lawdInfo);
    }

    @GetMapping(value="{lawdDir}/geom/lawd/file")
    public ResponseEntity<Resource> getLawdFileGeomDataToLawd(
            @PathVariable(value = "lawdDir") String lawdDir,
            @RequestParam(value = "lawdName") String lawdName
    ) {
        String fileName = "geom.json";
        String tradeInfo = fgmService.getGeomInfoToLawd(lawdName);

        return fileService.getDataFile(lawdDir, "", "", fileName, tradeInfo);
    }

    /**
     * 지역코드로 해당 지역 아파트 리스트 저장 - 단지코드 획득(국토교통부_공동주택 단지 목록제공 서비스)
     * @param lawdCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart")
    public ResponseEntity<MsgEntity> saveApartList(
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        fgmService.saveApart(lawdCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 단지코드로 아파트 주소 조회
     * @param lawdCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart/info")
    public ResponseEntity<MsgEntity> saveApartInfo(
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        fgmService.saveApartInfo(lawdCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 아파트 주소로 좌표 획득
     * @param lawdCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart/conv")
    public ResponseEntity<MsgEntity> saveApartConv(
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        fgmService.savecApartConv(lawdCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

}
