package com.app.findgwangmyeongserver.controller;

import com.app.findgwangmyeongserver.entity.MsgEntity;
import com.app.findgwangmyeongserver.service.FgmService;
import com.app.findgwangmyeongserver.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trade")
@RequiredArgsConstructor
public class FgmController {

    private final FgmService fgmService;
    private final FileService fileService;

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

        for (int month = 1; month <= 12; month++) {
            int result = fgmService.saveLatestTradeData(lawdCd, type, year, String.valueOf(month < 10 ? "0" + month : month));

            if (result > 0) isUpdate = true;
        }

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", isUpdate ? "Data has been updated" : "No data changed"));
    }

    @GetMapping(value="/master/{lawdDir}/file")
    public ResponseEntity<Resource> getMasterFile(
             @PathVariable("lawdDir") String lawdDir
    ) {
        String fileName   = "master.json";
        String masterList = fgmService.masterList();

        return fileService.getDataFile(lawdDir, "", "", fileName, masterList);
    }

    @GetMapping(value="/lawd/{lawdDir}/file")
    public ResponseEntity<Resource> getLawdFile(
             @PathVariable("lawdDir") String lawdDir
    ) {
        String fileName = "lawd.json";
        String lawdList = fgmService.lawdList();

        return fileService.getDataFile(lawdDir, "", "", fileName, lawdList);
    }

    @GetMapping(value="/subway/{lawdDir}/file")
    public ResponseEntity<Resource> getSubwayFile(
             @PathVariable("lawdDir") String lawdDir
    ) {
        String fileName = "subway.json";
        String subwayList = fgmService.getSubwayList();

        return fileService.getDataFile(lawdDir, "", "", fileName, subwayList);
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
                int result = fgmService.saveLatestTradeData(lawdCd, "deal", String.valueOf(year), String.valueOf(month < 10 ? "0" + month : month));
            }

            for (int month = 1; month <= 12; month++) {
                int result = fgmService.saveLatestTradeData(lawdCd, "rent", String.valueOf(year), String.valueOf(month < 10 ? "0" + month : month));
            }
        }

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 거래 내역을 조회해 아파트 리스트 정리
     * @param lawdCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart")
    public ResponseEntity<MsgEntity> saveApart(
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        fgmService.saveApart(lawdCd);
        fgmService.saveApartConv(lawdCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 시군구 아파트 목록 조회 후 저장
     * @param lawdCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart/code")
    public ResponseEntity<MsgEntity> saveApartCode(
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        fgmService.saveApartCode(lawdCd);
        fgmService.saveApartAddress(lawdCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 데이터 비교 - 거래 내역 아파트 리스트 : 아파트 목록
     * @param lawdCd
     * @return
     * @throws Exception
     */
    @PostMapping(value="/apart/compare")
    public ResponseEntity<MsgEntity> saveApartCompare(
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        fgmService.saveApartCompare1(lawdCd);
        fgmService.saveApartCompare2(lawdCd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    /**
     * 아파트 단지 파일 저장
     * @param lawdDir
     * @return
     */
    @GetMapping(value="/apart/{lawdDir}/file")
    public ResponseEntity<Resource> getApartListFile(
            @PathVariable("lawdDir") String lawdDir,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) {
        String fileName = "apart.json";
        String lawdInfo = fgmService.getApartList(lawdCd);

        return fileService.getDataFile(lawdDir, lawdCd, "", fileName, lawdInfo);
    }

}
