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

    @PutMapping(value="/{type}/latest/{year}/{month}")
    public ResponseEntity<MsgEntity> saveLatestTradeData(
            @PathVariable("type") String type,
            @PathVariable("year") String year,
            @PathVariable("month") String month,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        fgmService.saveLatestTradeData(lawdCd, type, year, month);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    @PostMapping(value="/{type}/save")
    public ResponseEntity<MsgEntity> saveTrade(
            @PathVariable("type") String type,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
            @RequestParam(value = "numOfRows", required = false, defaultValue = "0") int numOfRows,
            @RequestParam("dealYmd") String dealYmd,
            @RequestParam(value = "lawdCd", defaultValue = "41210") String lawdCd
    ) throws Exception {
        fgmService.saveTrade(type, lawdCd, pageNo, numOfRows, dealYmd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }


}
