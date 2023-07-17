package com.app.findgwangmyeongserver.controller;

import com.app.findgwangmyeongserver.entity.MsgEntity;
import com.app.findgwangmyeongserver.service.FgmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FgmController {

    private final FgmService fgmService;

    @PostMapping(value="/save")
    public ResponseEntity<MsgEntity> saveWord(
            @RequestParam(value = "pageNo", required = false) int pageNo,
            @RequestParam(value = "numOfRows", required = false) int numOfRows,
            @RequestParam("dealYmd") String dealYmd
    ) throws Exception {
        fgmService.saveData(pageNo, numOfRows, dealYmd);

        return ResponseEntity.ok()
                .body(new MsgEntity("OK", ""));

    }


}
