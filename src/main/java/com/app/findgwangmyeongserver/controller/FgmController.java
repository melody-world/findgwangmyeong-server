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
            @RequestParam("year") String year,
            @RequestParam("month") String month
    ) throws Exception {
        String fileName = year + "-" + month + ".json";
        String tradeInfo = fgmService.getTradeInfo(type, year, month);

        return fileService.getDataFile(type, fileName, tradeInfo);
    }

    @PutMapping(value="/{type}/latest/{year}/{month}")
    public ResponseEntity<MsgEntity> saveLatestTradeData(
            @PathVariable("type") String type,
            @PathVariable("year") String year,
            @PathVariable("month") String month
    ) throws Exception {
        fgmService.saveLatestTradeData(type, year, month);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    @PostMapping(value="/{type}/save")
    public ResponseEntity<MsgEntity> saveTrade(
            @PathVariable("type") String type,
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
            @RequestParam(value = "numOfRows", required = false, defaultValue = "0") int numOfRows,
            @RequestParam("dealYmd") String dealYmd
    ) throws Exception {
        fgmService.saveTrade(type, pageNo, numOfRows, dealYmd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }


}
