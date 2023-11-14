package com.app.findgwangmyeongserver.controller;

import com.app.findgwangmyeongserver.entity.ApartCodeEntity;
import com.app.findgwangmyeongserver.entity.MsgEntity;
import com.app.findgwangmyeongserver.service.FgmService;
import com.app.findgwangmyeongserver.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("trade")
@RequiredArgsConstructor
public class FgmController {

    private final FgmService fgmService;
    private final FileService fileService;

    /**
     * 월별 광명찾자_아파트 거래내역 최신화
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
        int result = fgmService.saveLatestTradeData(lawdCd, type, year, month, false);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", result == 1 ? "Data has been updated" : "No data changed"));
    }

    /**
     * 년도별 광명찾자_아파트 거래내역 최신화
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

        for (int month = 1; month <= 12; month++) {
            int result = fgmService.saveLatestTradeData(lawdCd, type, year, String.valueOf(month < 10 ? "0" + month : month), false);

            if (result > 0) isUpdate = true;
        }

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", isUpdate ? "Data has been updated" : "No data changed"));
    }

    @GetMapping(value="/master/{path}/file")
    public ResponseEntity<Resource> getMasterFile(
             @PathVariable("path") String path
    ) {
        String fileName   = "master.json";
        String masterList = fgmService.masterList();

        return fileService.getDataFile(path, "", fileName, masterList);
    }

    @GetMapping(value="/lawd/{path}/{masterDir}/file")
    public ResponseEntity<Resource> getLawdFile(
            @PathVariable("path") String path,
            @PathVariable("masterDir") String masterDir
    ) {
        String fileName = "lawd.json";
        String lawdList = fgmService.lawdList();

        return fileService.getDataFile(path, masterDir, fileName, lawdList);
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
     * 시작연도와 종료연도를 받아 매매,전월세 데이터를 정리
     * @param lawdCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/data")
    public ResponseEntity<MsgEntity> saveTrade(
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd,
            @RequestParam(value = "fromYear") String fromYear,
            @RequestParam(value = "toYear") String toYear
    ) throws Exception {
        int start = Integer.parseInt(fromYear);
        int end = Integer.parseInt(toYear);

        for (int year = start; year <= end; year++) {
            for (int month = 1; month <= 12; month++) {
                int result = fgmService.saveLatestTradeData(lawdCd, "deal", String.valueOf(year), String.valueOf(month < 10 ? "0" + month : month), true);
            }

            for (int month = 1; month <= 12; month++) {
                int result = fgmService.saveLatestTradeData(lawdCd, "rent", String.valueOf(year), String.valueOf(month < 10 ? "0" + month : month), true);
            }
        }

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
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
            @RequestParam(value = "lawdCd", required = false) String lawdCd
    ) throws Exception {
        fgmService.saveApartAddress(masterCd, lawdCd);

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





}
