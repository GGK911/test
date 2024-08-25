package springTest.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 测试接口
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/21 10:53
 */
@RestController
@RequiredArgsConstructor
public class TestController {

    @SneakyThrows
    @RequestMapping("/helloworld")
    public String test(HttpServletRequest request) {
        System.out.println("**helloWorld**");
        return "helloWorld";
    }

    @SneakyThrows
    @RequestMapping("/formTest")
    public String formTest(String param) {
        return "param:" + param;
    }

    @SneakyThrows
    @RequestMapping("/jsonTest")
    public String jsonTest(@RequestBody String jsonRaw) {
        return "json:" + jsonRaw;
    }

}
