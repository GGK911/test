package springTest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springTest.service.ScapServiceImpl;

/**
 * @author TangHaoKai
 * @version V1.0 2024/11/11 10:15
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ScapController {
    private final ScapServiceImpl scapService;

    @RequestMapping("/scap/signP1")
    public String signP1(String waitSignData) {
        return scapService.signP1(waitSignData);
    }

    @RequestMapping("/scap/signP7")
    public String signP7(String waitSignData, String cert) {
        return scapService.signP7(waitSignData, cert);
    }
}
