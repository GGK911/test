package springTest.service;

import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.pki.core.util.SignatureUtil;
import cn.com.mcsca.util.signature.SignatureSignUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pdfTest.PdfUtil;
import sealTest.CreateSealUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通用公共服务
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-23 10:50
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonServiceImpl {

    /**
     * 生成签名值(pki-core)
     *
     * @param priKey  加签私钥
     * @param reqHead 请求头
     * @param reqBody 请求体
     * @return 签名json
     */
    public Object createSignValue(String priKey, String reqHead, String reqBody) {
        log.info("开始生产签名值，priKey={},reqHead={},reqBody={}", priKey, reqHead, reqBody);
        Map<String, String> reqMap = new HashMap<>(2);
        reqMap.put("reqHead", reqHead);
        reqMap.put("reqBody", reqBody);
        log.info("签名私钥：{}", priKey);
        String jsonString = com.alibaba.fastjson.JSONObject.toJSONString(reqMap, SerializerFeature.MapSortField, SerializerFeature.SortField);
        String sign = "";
        try {
            log.info("签名原文：{}", jsonString);
            sign = SignatureUtil.doSign(priKey, jsonString);
        } catch (Exception e) {
            log.error("签名异常");
            e.printStackTrace();
        }
        log.info("签名值...{}", sign);
        return sign;
    }

    /**
     * 生成签名值(pki-base)
     *
     * @param priKey  加签私钥
     * @param reqHead 请求头
     * @param reqBody 请求体
     * @return 签名json
     */
    public Object createSignValue2(String priKey, String reqHead, String reqBody) {
        log.info("开始生产签名值，priKey={},reqHead={},reqBody={}", priKey, reqHead, reqBody);
        Map<String, String> reqMap = new HashMap<>(2);
        reqMap.put("reqHead", reqHead);
        reqMap.put("reqBody", reqBody);
        log.info("签名私钥：{}", priKey);
        String jsonString = JSONUtil.toJsonStr(reqMap);
        String sign = "";
        try {
            log.info("签名原文：{}", jsonString);
            sign = new SecuEngine().SignDataBySM2(priKey, jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("签名异常");
            e.printStackTrace();
        }
        log.info("签名值...{}", sign);
        return sign;
    }

    public Object createSignValue4(String priKey, String reqHead, String reqBody) {
        log.info("开始生产签名值，priKey={},reqHead={},reqBody={}", priKey, reqHead, reqBody);
        Map<String, Object> reqMap = new HashMap<>(2);
        Map<String, Object> reqHeadMap = toMap(reqHead);
        Map<String, Object> reqBodyMap = toMap(reqBody);
        reqMap.put("reqHead", reqHeadMap);
        reqMap.put("reqBody", reqBodyMap);
        log.info("签名私钥：{}", priKey);

        String jsonString = SignatureUtil.getSortJsonStr(reqMap);
        String sign = "";
        try {
            log.info("签名原文：{}", jsonString);
            sign = new SecuEngine().SignDataBySM2(priKey, jsonString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("签名异常");
            e.printStackTrace();
        }
        log.info("签名值...{}", sign);
        return sign;
    }

    public static Map<String, Object> toMap(String json) {
        Map<String, Object> map = null;
        try {
            map = toMap(JSON.parseObject(json));
        } catch (Exception e) {
            throw new RuntimeException("转换JSON时错误");
        }
        return map;
    }

    public static Map<String, Object> toMap(com.alibaba.fastjson.JSONObject json) {
        Map<String, Object> map = new HashMap<>();
        Set<Map.Entry<String, Object>> entrySet = json.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof JSONArray) {
                map.put(key, toList((JSONArray) value));
            } else if (value instanceof com.alibaba.fastjson.JSONObject) {
                map.put(key, toMap((com.alibaba.fastjson.JSONObject) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    public static List<Object> toList(JSONArray json) {
        List<Object> list = new ArrayList<>();
        for (Object value : json) {
            if (value instanceof JSONArray) {
                list.add(toList((JSONArray) value));
            } else if (value instanceof com.alibaba.fastjson.JSONObject) {
                list.add(toMap((com.alibaba.fastjson.JSONObject) value));
            } else {
                list.add(value);
            }
        }
        return list;
    }

    public Object createSignValue(String priKey, String reqParam) {
        log.info("开始生产签名值，priKey={},reqParam={}", priKey, reqParam);
        log.info("签名私钥：{}", priKey);
        com.alibaba.fastjson.JSONObject requestMap = com.alibaba.fastjson.JSONObject.parseObject(reqParam);
        Map<String, Object> map = new HashMap<>();
        map.put("reqHead", requestMap.get("reqHead"));
        map.put("reqBody", requestMap.get("reqBody"));
        String text = SignatureSignUtil.getSortJsonStr(map);
        String sign = "";
        try {
            log.info("签名原文：{}", text);
            sign = new SecuEngine().SignDataBySM2(priKey, text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("签名异常");
            e.printStackTrace();
        }
        log.info("签名值...{}", sign);

        JSONObject res = new JSONObject();
        res.set("sign", sign);
        res.set("oriText", text);
        return res.toString();
    }

    public Object createSignValueByHutoolJson(String priKey, String reqParam) {
        log.info("开始生产签名值，priKey={},reqParam={}", priKey, reqParam);
        log.info("签名私钥：{}", priKey);
        com.alibaba.fastjson.JSONObject requestMap = com.alibaba.fastjson.JSONObject.parseObject(reqParam);
        Map<String, Object> map = new HashMap<>();
        map.put("reqHead", requestMap.get("reqHead"));
        map.put("reqBody", requestMap.get("reqBody"));
        String text = JSONUtil.toJsonStr(map);
        String sign = "";
        try {
            log.info("签名原文：{}", text);
            sign = new SecuEngine().SignDataBySM2(priKey, text.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("签名异常");
            e.printStackTrace();
        }
        log.info("签名值...{}", sign);
        JSONObject res = new JSONObject();
        res.set("sign", sign);
        res.set("oriText", text);
        return res.toString();
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
    public Object verifySignValue(String pubKey, String reqHead, String reqBody, String signValue) {
        log.info("开始验证签名值");
        Map<String, String> reqMap = new HashMap<>(2);
        reqMap.put("reqHead", reqHead);
        reqMap.put("reqBody", reqBody);
        String jsonString = com.alibaba.fastjson.JSONObject.toJSONString(reqMap, SerializerFeature.MapSortField, SerializerFeature.SortField);
        log.info("验签原文：{}", jsonString);
        log.info("验签公钥：{}", pubKey);
        boolean verify = false;
        try {
            SecuEngine secuEngine = new SecuEngine();
            verify = secuEngine.VerifySignDataWithSM2ByPublicKey(pubKey, jsonString.getBytes(), signValue);
        } catch (Exception e) {
            log.error("验签异常");
            e.printStackTrace();
        }
        Map<String, Object> resBody = new HashMap<>(1);
        resBody.put("verify", verify);
        log.info("验签结果...{}", verify);
        return JSONUtil.parse(resBody);
    }

    public Object verifySignValue(String pubKey, String reqParam) {
        log.info("开始验证签名值");
        com.alibaba.fastjson.JSONObject requestMap = com.alibaba.fastjson.JSONObject.parseObject(reqParam);
        String signValue = (String) requestMap.get("sign");
        log.info("signValue：{}", signValue);
        Map<String, Object> reqMap = new HashMap<>(2);
        reqMap.put("reqHead", requestMap.get("reqHead"));
        reqMap.put("reqBody", requestMap.get("reqBody"));
        String text = SignatureSignUtil.getSortJsonStr(reqMap);
        log.info("第一次验签原文：{}", text);
        log.info("验签公钥：{}", pubKey);
        boolean verify = false;
        try {
            SecuEngine secuEngine = new SecuEngine();
            verify = secuEngine.VerifySignDataWithSM2ByPublicKey(pubKey, text.getBytes(StandardCharsets.UTF_8), signValue);
            if (!verify) {
                text = JSONUtil.toJsonStr(JSONUtil.parse(requestMap));
                log.info("第二次验签原文：{}", text);
                verify = secuEngine.VerifySignDataWithSM2ByPublicKey(pubKey, text.getBytes(StandardCharsets.UTF_8), signValue);
            }
        } catch (Exception e) {
            log.error("验签异常");
            e.printStackTrace();
        }
        Map<String, Object> resBody = new HashMap<>(1);
        resBody.put("verify", verify);
        log.info("验签结果...{}", verify);
        return JSONUtil.parse(resBody);
    }

    /**
     * 生成个人方章
     *
     * @param name     名称
     * @param response 响应
     */
    @SneakyThrows
    public void createPersonSeal(String name, HttpServletResponse response) {
        log.info("开始生成图章，name={}", name);
        byte[] sealBytes = CreateSealUtil.createSquareSeal(name);
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(sealBytes);
        } catch (Exception e) {
            log.error("文件响应失败", e);
            e.printStackTrace();
        }
    }

    /**
     * 生成企业圆章
     *
     * @param name     企业名称
     * @param response 响应
     */
    @SneakyThrows
    public void createEnterpriseSeal(String name, HttpServletResponse response) {
        log.info("开始生成图章，name={}", name);
        byte[] sealBytes = CreateSealUtil.createCircleSeal(name);
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(sealBytes);
        } catch (Exception e) {
            log.error("文件响应失败", e);
            e.printStackTrace();
        }
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
    @SneakyThrows
    public void pdfFill(MultipartFile picFile, MultipartFile pdfFile, String textDomainParams, String picDomainParams, HttpServletResponse response) {
        JSONObject textParamsJson = JSONUtil.parseObj(textDomainParams);
        JSONObject picParamsJson = JSONUtil.parseObj(picDomainParams);
        Map<String, Object> params = new HashMap<>();
        params.putAll(textParamsJson.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue()))));
        params.putAll(picParamsJson.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> JSONUtil.toBean(String.valueOf(entry.getValue()), PdfUtil.FillImageParam.class))));
        byte[] pdfFileBytes = pdfFile.getBytes();
        byte[] picFileBytes = picFile.getBytes();
        byte[] pdfFill = PdfUtil.pdfFill(pdfFileBytes, params, picFileBytes);
        try (ServletOutputStream out = response.getOutputStream()) {
            out.write(pdfFill);
        }
    }

    /**
     * 读取PDF域参数
     *
     * @param pdfFile pdf文件
     * @return 参数信息
     */
    @SneakyThrows
    public JSONObject pdfParams(MultipartFile pdfFile) {
        byte[] pdfFileBytes = pdfFile.getBytes();
        List<PdfUtil.PdfParameterEntity> params = PdfUtil.getPdfDomain(pdfFileBytes);
        JSONObject res = new JSONObject();
        res.set("params", params);
        return res;
    }

    public Object createSignValue5(String mode, String priKey, String reqParam) {
        if (mode.equalsIgnoreCase("mcsca")) {
            return createSignValue(priKey, reqParam);
        } else {
            return createSignValueByHutoolJson(priKey, reqParam);
        }
    }
}
