package springTest.controller;

import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 测试接口
 *
 * @author TangHaoKai
 * @version V1.0 2024/8/21 10:53
 */
@Api(tags = "1.0.0版本-20241106")
@ApiSupport(order = 284)
@RestController
@RequiredArgsConstructor
public class TestController {

    @SneakyThrows
    @RequestMapping("/helloworld")
    public String test(HttpServletRequest request) {
        System.out.println("**helloWorld**");
        return "helloWorld";
    }

    @ApiOperation("测试form表单接口")
    @SneakyThrows
    @RequestMapping("/formTest")
    public String formTest(@ApiParam("接收参数") String param) {
        return "param:" + param;
    }

    @SneakyThrows
    @RequestMapping("/jsonTest")
    public String jsonTest(@RequestBody String jsonRaw) {
        return "json:" + jsonRaw;
    }

    @RequestMapping("/asyncTest")
    public String asyncTest(String a, String b) {
        try {
            Thread.sleep(3000);
            System.out.println("醒了");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return a + b;
    }

    @RequestMapping("/asyncTest2")
    public String asyncTest2(String a, String b) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("醒了");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return a + b;
    }

}
