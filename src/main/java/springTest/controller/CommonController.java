package springTest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springTest.service.CommonServiceImpl;

import javax.servlet.http.HttpServletResponse;

/**
 * 通用
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-22 17:43
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommonController {
    private final CommonServiceImpl commonService;

    /**
     * 生成签名值(pki-core)
     *
     * @param priKey  加密私钥
     * @param reqHead 请求头
     * @param reqBody 请求体
     * @return 签名json
     */
    @PostMapping(value = "/common/signValue")
    public Object createSignValue(@RequestParam("priKey") String priKey,
                                  @RequestParam("reqHead") String reqHead,
                                  @RequestParam("reqBody") String reqBody) {
        return commonService.createSignValue(priKey, reqHead, reqBody);
    }

    /**
     * 生成签名值(pki-base)
     *
     * @param priKey  加密私钥
     * @param reqHead 请求头
     * @param reqBody 请求体
     * @return 签名json
     */
    @PostMapping(value = "/common/signValue2")
    public Object createSignValue2(@RequestParam("priKey") String priKey,
                                   @RequestParam("reqHead") String reqHead,
                                   @RequestParam("reqBody") String reqBody) {
        return commonService.createSignValue2(priKey, reqHead, reqBody);
    }

    @PostMapping(value = "/common/signValue3")
    public Object createSignValue(@RequestParam("priKey") String priKey,
                                  @RequestParam("reqParam") String reqParam) {
        return commonService.createSignValue(priKey, reqParam);
    }

    @PostMapping(value = "/common/signValue4")
    public Object createSignValue4(@RequestParam("priKey") String priKey,
                                   @RequestParam("reqHead") String reqHead,
                                   @RequestParam("reqBody") String reqBody) {
        return commonService.createSignValue4(priKey, reqHead, reqBody);
    }

    @PostMapping(value = "/common/signValue5")
    public Object createSignValue5(@RequestParam("mode") String mode,
                                   @RequestParam("priKey") String priKey,
                                   @RequestParam("reqParam") String reqParam) {
        return commonService.createSignValue5(mode, priKey, reqParam);
    }


    /**
     * 验签
     *
     * @param pubKey    公钥
     * @param reqHead   请求头
     * @param reqBody   请求体
     * @param signValue 签名
     * @return 验签结果
     */
    @PostMapping(value = "/common/verifySignValue")
    public Object verifySignValue(@RequestParam("pubKey") String pubKey,
                                  @RequestParam("reqHead") String reqHead,
                                  @RequestParam("reqBody") String reqBody,
                                  @RequestParam("signValue") String signValue) {
        return commonService.verifySignValue(pubKey, reqHead, reqBody, signValue);
    }

    @PostMapping(value = "/common/verifySignValue2")
    public Object verifySignValue(@RequestParam("pubKey") String pubKey,
                                  @RequestParam("reqParam") String reqParam) {
        return commonService.verifySignValue(pubKey, reqParam);
    }

    /**
     * 生成个人方章
     *
     * @param name     名称
     * @param response 响应
     */
    @PostMapping(value = "/common/seal/personSeal")
    public void createPersonSeal(@RequestParam("name") String name,
                                 HttpServletResponse response) {
        commonService.createPersonSeal(name, response);
    }

    /**
     * 生成企业圆章
     *
     * @param name     企业名称
     * @param response 响应
     */
    @PostMapping(value = "/common/seal/enterpriseSeal")
    public void createEnterpriseSeal(@RequestParam("name") String name,
                                     HttpServletResponse response) {
        commonService.createEnterpriseSeal(name, response);
    }

    /**
     * 填充PDF
     *
     * @param picFile          图片File
     * @param pdfFile          pdfFile
     * @param textDomainParams 文本域参数
     * @param picDomainParams  图片域参数
     * @param response         响应
     */
    @PostMapping(value = "/common/pdf/fill")
    public void pdfFill(MultipartFile picFile, MultipartFile pdfFile, String textDomainParams, String picDomainParams, HttpServletResponse response) {
        commonService.pdfFill(picFile, pdfFile, textDomainParams, picDomainParams, response);
    }

    /**
     * 读取PDF域参数
     *
     * @param pdfFile pdf文件
     * @return 参数信息
     */
    @PostMapping(value = "/common/pdf/params")
    public Object pdfParams(MultipartFile pdfFile) {
        return commonService.pdfParams(pdfFile);
    }
}
