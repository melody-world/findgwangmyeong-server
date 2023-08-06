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

    @GetMapping(value="{type}/file")
    public ResponseEntity<Resource> getFileTradeData(
            @PathVariable("type") String type,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd,
            @RequestParam("year") String year,
            @RequestParam("month") String month
    ) {
        String fileName = year + "-" + month + ".json";
        String tradeInfo = fgmService.getTradeInfo(lawdCd, type, year, month);

        return fileService.getDataFile(lawdCd, type, fileName, tradeInfo);
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

    @PostMapping(value="/geom")
    public ResponseEntity<MsgEntity> saveGeom() throws Exception {
        fgmService.saveGeom();

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    @GetMapping(value="/geom/file")
    public ResponseEntity<Resource> getFileGeomData(
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd,
            @RequestParam("name") String name
    ) {
        String fileName = "geom.json";
        String tradeInfo = fgmService.geomInfo(name);

        return fileService.getDataFile(lawdCd, "", fileName, tradeInfo);
    }

    @GetMapping(value="/file/lawd")
    public ResponseEntity<Resource> getFileLawd() {
        String fileName = "lawd.json";
        String lawdInfo = fgmService.lawdList();

        return fileService.getDataFile("", "", fileName, lawdInfo);
    }


}
