package com.app.findgwangmyeongserver.controller;

import com.app.findgwangmyeongserver.entity.MsgEntity;
import com.app.findgwangmyeongserver.service.FgmService;
import com.app.findgwangmyeongserver.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FgmController {

    private final FgmService fgmService;
    private final FileService fileService;

    @GetMapping(value="/trade")
    public ResponseEntity<Resource> getTradeData(
            @RequestParam("year") String year,
            @RequestParam("month") String month
    ) throws Exception {
        String fileName = year + "-" + month + ".json";
        String tradeInfo = fgmService.getTradeInfo(year, month);

        if (fileService.createFile(fileName, tradeInfo)) {
            return fileService.downloadFile(fileName);
        } else {
            return ResponseEntity.internalServerError()
                        .body(null);
        }
    }

    @PostMapping(value="/trade/confirm")
    public ResponseEntity<MsgEntity> confirmTrade(
            @RequestParam("year") String year,
            @RequestParam("month") String month
    ) throws Exception {
        fgmService.confirmTrade(year, month);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }

    @PostMapping(value="/trade/save")
    public ResponseEntity<MsgEntity> saveTrade(
            @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo,
            @RequestParam(value = "numOfRows", required = false, defaultValue = "0") int numOfRows,
            @RequestParam("dealYmd") String dealYmd
    ) throws Exception {
        fgmService.saveData(pageNo, numOfRows, dealYmd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));
    }


}
