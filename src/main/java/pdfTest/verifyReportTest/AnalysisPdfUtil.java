package pdfTest.verifyReportTest;

import cn.com.mcsca.cms.sdk.CmsClient;
import cn.com.mcsca.cms.sdk.model.Config;
import cn.com.mcsca.cms.sdk.model.req.CertQueryRequest;
import cn.com.mcsca.cms.sdk.model.req.TokenRequest;
import cn.com.mcsca.cms.sdk.model.res.CertQueryResponse;
import cn.com.mcsca.cms.sdk.model.res.TokenResponse;
import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.itextpdf.text.pdf.AcroFields;
import cn.com.mcsca.itextpdf.text.pdf.PdfReader;
import cn.com.mcsca.itextpdf.text.pdf.security.PdfPKCS7;
import cn.com.mcsca.pdf.signature.PdfVerify;
import cn.com.mcsca.util.CertUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author 文仔
 */
public class AnalysisPdfUtil {
    public static final String ALG_TYPE = "SHA256";
    static String pri = "MIICBQIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////v////////////////////8AAAAA//////////8wRAQg/////v////////////////////8AAAAA//////////wEICjp+p6dn140TVqeS89lCafzl4n1FauPkt28vUFNlA6TBEEEMsSuLB8ZgRlfmQRGajnJlI/jC7/yZgvhcVpFiTNMdMe8Nzai9PZ3nFm9zuNraSFT0KmHfMYqR0AC3zLlITnwoAIhAP////7///////////////9yA99rIcYFK1O79Ak51UEjAgEBBIIBDzCCAQsCAQEEIDR3bAiC8dEC0wWYNKqhqJR+oJwiWcTlKYNcM661iDm/oIHjMIHgAgEBMCwGByqGSM49AQECIQD////+/////////////////////wAAAAD//////////zBEBCD////+/////////////////////wAAAAD//////////AQgKOn6np2fXjRNWp5Lz2UJp/OXifUVq4+S3by9QU2UDpMEQQQyxK4sHxmBGV+ZBEZqOcmUj+MLv/JmC+FxWkWJM0x0x7w3NqL09necWb3O42tpIVPQqYd8xipHQALfMuUhOfCgAiEA/////v///////////////3ID32shxgUrU7v0CTnVQSMCAQE=";
    static String appId = "mcsca-99vrgrd7ml";
    static String appSecret = "lxn0oby45b";
    static String endpoint = "http://183.66.184.22:803/cms-ora-interface";

    // private final CmsProperties cmsProperties;
    // private final RootCertDao rootCertDao;
    //
    // public AnalysisPdfUtil(CmsProperties cmsProperties, RootCertDao rootCertDao) {
    //     this.cmsProperties = cmsProperties;
    //     this.rootCertDao = rootCertDao;
    // }

    /**
     * 电子合同验证
     *
     * @param multipartFile 电子合同文件
     * @return 验证结果
     * @throws Exception 异常
     */
    public static ReportEntity verifyBase(byte[] multipartFile, String fileName) throws Exception {
        //构建电子签名验证报告的基本信息
        ReportEntity reportEntity = new ReportEntity();
        ArrayList<ReportEntity.ReportSignEntity> signInfoList = new ArrayList<>();

        //开始解析签章信息
        for (PdfVerify.SignatureInfo signatureInfo : analysisSignInfo(multipartFile)) {
            ReportEntity.ReportSignEntity reportSignEntity = new ReportEntity.ReportSignEntity();

            //解析签章的基本信息
            reportSignEntity.setSignatureName(signatureInfo.getSignatureName());
            reportSignEntity.setSignaturePage(signatureInfo.getSignaturePage() + "");
            reportSignEntity.setSignDate(signatureInfo.getSignDate());
            reportSignEntity.setSealData(signatureInfo.getSealData());

            //解析签章证书的基本详细
            byte[] signCertByte = signatureInfo.getPublicCertChain()[0].getEncoded();
            String signCertStartData = CertUtil.parseCert(signCertByte, CertUtil.START_DATE);
            reportSignEntity.setSignCertSubject(CertUtil.parseCert(signCertByte, CertUtil.SUBJECT));
            // 证书序列号
            reportSignEntity.setSignCertSn(CertUtil.parseCert(signCertByte, CertUtil.SERIAL_NUMBER));
            // thk's todo 2024/3/25 14:12 通过证书序列号向上查询到用户认证记录
            reportSignEntity.setAuthType("运营商三要素");
            reportSignEntity.setAuthTime("2024-03-25 00:00:00");
            reportSignEntity.setUserName("唐好凯");
            reportSignEntity.setUserCardNo("371724200206052210");
            reportSignEntity.setUserPhone("13983053455");
            reportSignEntity.setAuthResult("认证通过");
            reportSignEntity.setAuthSmsCode("112233");
            reportSignEntity.setAuthSmsSendTime("2024-03-25 00:00:01");
            reportSignEntity.setAuthSmsBackFillCode("112233");
            reportSignEntity.setAuthSmsBackFillTime("2024-03-25 00:00:02");

            reportSignEntity.setWillType("短信意愿");
            reportSignEntity.setWillTime("2024-03-25 00:00:00");
            reportSignEntity.setWillResult("发送成功");
            reportSignEntity.setWillSmsCode("112233");
            reportSignEntity.setWillSmsSendTime("2024-03-25 00:00:00");
            reportSignEntity.setWillSmsBackFillCode("112233");
            reportSignEntity.setWillSmsBackFillTime("2024-03-25 00:00:00");

            reportSignEntity.setReadProtocolEndTime("2024-03-25 00:00:00");
            reportSignEntity.setReadProtocolContent("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

            reportSignEntity.setSignCertIssuer(CertUtil.parseCert(signCertByte, CertUtil.ISSUER));
            reportSignEntity.setSignCertStartData(signCertStartData);
            reportSignEntity.setSignCertEndData(CertUtil.parseCert(signCertByte, CertUtil.END_DATE));
            // reportSignEntity.setSignCertVerifyRootCert(signCertVerifyRootCert(signCertByte));
            // reportSignEntity.setMcscaCert(isMcscaCert(signCertByte));
            // if (reportSignEntity.isMcscaCert()) {
            if (true) {
                reportSignEntity.setSignCertVerifyCrl(signCertVerifyCrl(CertUtil.parseCert(signCertByte, CertUtil.SERIAL_NUMBER)));
            } else {
                reportSignEntity.setSignCertVerifyCrl(true);
            }
            reportSignEntity.setSignDateVerifyBySignCert(verifySignDateBetween(CertUtil.parseCert(signCertByte, CertUtil.START_DATE), CertUtil.parseCert(signCertByte, CertUtil.END_DATE), signatureInfo.getSignDate()));

            //解析时间戳的基本详细
            byte[] timeStampCert = signatureInfo.getTimeStampCert();
            if (ObjectUtil.isNull(timeStampCert) || ObjectUtil.isEmpty(timeStampCert)) {
                reportSignEntity.setContainTs(false);
                //当证书为大陆云盾证书（不存在时间戳）并且签章时间不在签章证书的有效期内
                if (reportSignEntity.isMcscaCert() && !reportSignEntity.isSignDateVerifyBySignCert()) {
                    // 签署时间 是否在 证书生效时间(容错2min)和证书结束时间之内
                    if (verifySignDateBetween(DateUtil.format(DateUtil.offset(DateUtil.parse(signCertStartData), DateField.MINUTE, -2), DatePattern.PURE_DATETIME_MS_PATTERN), CertUtil.parseCert(signCertByte, CertUtil.END_DATE), signatureInfo.getSignDate())) {
                        //在容错2min内报告中签署时间的值取证书生效时间值
                        reportSignEntity.setSignDate(signCertStartData);
                        reportSignEntity.setSignDateVerifyBySignCert(true);
                    }
                }
            } else {
                reportSignEntity.setContainTs(true);
                reportSignEntity.setTsCertSubject(CertUtil.parseCert(timeStampCert, CertUtil.SUBJECT));
                reportSignEntity.setTsCertSn(CertUtil.parseCert(timeStampCert, CertUtil.SERIAL_NUMBER));
                reportSignEntity.setTsCertIssuer(CertUtil.parseCert(timeStampCert, CertUtil.ISSUER));
                reportSignEntity.setTsCertStartData(CertUtil.parseCert(timeStampCert, CertUtil.START_DATE));
                reportSignEntity.setTsCertEndData(CertUtil.parseCert(timeStampCert, CertUtil.END_DATE));
                //获取时间戳时间 --- pki-bases包还未解析时间戳时间
                AcroFields acroFields = new PdfReader(multipartFile, null).getAcroFields();
                PdfPKCS7 pkcs7 = acroFields.verifySignature(signatureInfo.getSignatureName());
                String tsSignDate = new SimpleDateFormat(DatePattern.PURE_DATETIME_MS_PATTERN).format(pkcs7.getTimeStampToken().getTimeStampInfo().getGenTime());
                reportSignEntity.setTsSignDate(tsSignDate);
                reportSignEntity.setSignDateVerifyByTsCert(verifySignDateBetween(CertUtil.parseCert(timeStampCert, CertUtil.START_DATE), CertUtil.parseCert(timeStampCert, CertUtil.END_DATE), tsSignDate));
                reportSignEntity.setMcscaTs("MCSCA".equalsIgnoreCase(signatureInfo.getTimeStamper()));
                //当证书为大陆云盾证书（存在大陆云盾时间戳）
                if (reportSignEntity.isMcscaCert() && "MCSCA".equalsIgnoreCase(signatureInfo.getTimeStamper())) {
                    //大陆云盾的证书、并且有使用大陆云盾时间戳进行签署的情况下，报告中签署时间的值取时间戳的值
                    reportSignEntity.setSignDate(tsSignDate);
                    // 签署时间(时间戳时间) 是否在 证书生效时间(容错1s)和证书结束时间之内
                    reportSignEntity.setSignDateVerifyBySignCert(verifySignDateBetween(DateUtil.format(DateUtil.offset(DateUtil.parse(signCertStartData), DateField.SECOND, -1), DatePattern.PURE_DATETIME_MS_PATTERN), CertUtil.parseCert(signCertByte, CertUtil.END_DATE), tsSignDate));
                }
            }

            //当前签章验证结果分析:是否验证通过，签章是否是否被篡改
            //判断文件签章的是否验证通过，通过才判断是否被修改
            if (signatureInfo.isValidate()) {
                reportSignEntity.setModify(signatureInfo.isModify());
            } else {
                reportSignEntity.setModify(true);
            }

            reportSignEntity.setVerifyResult(verifyResult(signatureInfo, reportSignEntity));
            signInfoList.add(reportSignEntity);
        }
        reportEntity.setSignInfoList(signInfoList);
        reportEntity.setFileName(fileName);
        onlyMcscaCert(reportEntity);
        genReportBaseInfo(reportEntity, multipartFile);
        return reportEntity;
    }

    /**
     * 解析电子签章文档中的签章信息
     *
     * @param multipartFile 合同文件
     * @return 签章信息
     */
    private static List<PdfVerify.SignatureInfo> analysisSignInfo(byte[] multipartFile) {
        List<PdfVerify.SignatureInfo> signatureInfos = new ArrayList<>();
        //解析标准格式的pdf文件
        try {
            signatureInfos = PdfVerify.doVerify(multipartFile);
        } catch (Exception ignored) {
        }

        //解析国密标准版电子签章
        if (signatureInfos.size() == 0) {
            try {
                signatureInfos = PdfVerify.doVerifyGmSign(multipartFile);
            } catch (Exception ignored) {
            }
        }

        //解析北京ca的自定义pdf文件
        if (signatureInfos.size() == 0) {
            try {
                signatureInfos = PdfVerify.doVerifyBJCASig(multipartFile);
            } catch (Exception ignored) {
            }
        }

        if (signatureInfos.size() == 0) {
            throw new IllegalArgumentException("该电子文件无签章信息");
        }

        return signatureInfos;
    }

    /**
     * 判读当前文件的电子签章是否全部是使用的大陆云盾的证书
     */
    private static void onlyMcscaCert(ReportEntity reportEntity) {
        boolean onlyMcscaCert = true;
        ArrayList<ReportEntity.ReportSignEntity> signInfoList = reportEntity.getSignInfoList();
        for (ReportEntity.ReportSignEntity reportSignEntity : signInfoList) {
            if (!reportSignEntity.isMcscaCert()) {
                onlyMcscaCert = false;
                break;
            }
        }
        reportEntity.setOnlyMcscaCert(onlyMcscaCert);
    }

    /**
     * 获取当前文档的基本信息
     *
     * @param reportEntity  reportEntity
     * @param multipartFile 原始文档
     * @throws Exception 异常
     */
    private static void genReportBaseInfo(ReportEntity reportEntity, byte[] multipartFile) throws Exception {
        int signFalseNum = 0;
        boolean verifyModify = false;

        ArrayList<ReportEntity.ReportSignEntity> signInfoList = reportEntity.getSignInfoList();
        for (ReportEntity.ReportSignEntity reportSignEntity : signInfoList) {
            if (!reportSignEntity.isVerifyResult()) {
                signFalseNum += 1;
            }
            if (reportSignEntity.isModify() && !verifyModify) {
                verifyModify = true;
            }
        }
        reportEntity.setFileType(FileUtil.extName(reportEntity.getFileName()));
        reportEntity.setAlgType(ALG_TYPE);
        reportEntity.setFileHash(new SecuEngine().DigestData(multipartFile, ALG_TYPE).toLowerCase());
        reportEntity.setSignTotalNum(signInfoList.size());
        reportEntity.setSignTrueNum(signInfoList.size() - signFalseNum);
        reportEntity.setSignFalseNum(signFalseNum);
        reportEntity.setVerifyModify(verifyModify);
        reportEntity.setPass(signFalseNum == 0 && !verifyModify);
    }

    /**
     * 验证签署时间是否在证书的有效期内
     *
     * @param startDate 证书起始时间
     * @param endDate   证书结束时间
     * @param current   签署时间
     * @return boolean
     * @throws Exception 异常
     */
    private static boolean verifySignDateBetween(String startDate, String endDate, String current) throws Exception {
        Date currentDateTime = DateUtil.parse(current);
        Date startDateTime = DateUtil.parse(startDate);
        Date endDateTime = DateUtil.parse(endDate);

        assert currentDateTime != null;
        assert startDateTime != null;
        assert endDateTime != null;
        if (currentDateTime.getTime() >= startDateTime.getTime()) {
            return currentDateTime.getTime() <= endDateTime.getTime();
        } else {
            return false;
        }
    }

    /**
     * 验证当前签章信息是否有效
     *
     * @param signatureInfo    签章解析信息
     * @param reportSignEntity 签章验证详细信息
     * @return 有效or无效
     */
    private static boolean verifyResult(PdfVerify.SignatureInfo signatureInfo, ReportEntity.ReportSignEntity reportSignEntity) {
        boolean validate = signatureInfo.isValidate();
        boolean signCertVerifyRootCert = reportSignEntity.isSignCertVerifyRootCert();
        boolean signCertVerifyCrl = reportSignEntity.isSignCertVerifyCrl();
        boolean signDateVerifyBySignCert = reportSignEntity.isSignDateVerifyBySignCert();
        boolean signDateVerifyByTsCert = ObjectUtil.isNull(reportSignEntity.getSignDateVerifyByTsCert()) || reportSignEntity.getSignDateVerifyByTsCert();
        boolean modify = signatureInfo.isModify();
        return validate && signCertVerifyRootCert && signCertVerifyCrl && signDateVerifyBySignCert && signDateVerifyByTsCert && !modify;
    }


    /**
     * 根证书验证
     *
     * @param certByte 证书
     */
    // private boolean signCertVerifyRootCert(byte[] certByte) {
    //     if (permit(certByte)) {
    //         return true;
    //     }
    //     //获取当前证书的授权密钥标识符
    //     String authorityKeyIdent;
    //     try {
    //         authorityKeyIdent = CertUtil.parseCert(certByte, CertUtil.AUTHORITY_KEY_IDENTIFIER);
    //     } catch (Exception e) {
    //         throw new IllegalArgumentException("当前签章证书无授权密钥标识符");
    //     }
    //
    //     RootCertEntity rootCertEntity = rootCertDao.findBySubjectKeyIdentifier(authorityKeyIdent);
    //     if (ObjectUtil.isEmpty(rootCertEntity) || ObjectUtil.isNull(rootCertEntity)) {
    //         throw new IllegalArgumentException("文档中有签章证书未找到根证书，有效性无法验证");
    //     }
    //
    //     try {
    //         return CertUtil.verifyCertificate(certByte, Base64.decode(rootCertEntity.getCertBase64()));
    //     } catch (Exception e) {
    //         throw new IllegalArgumentException("证书信任链验证异常");
    //     }
    // }

    /**
     * 验证当前签章证书是否是大陆云盾颁发的
     *
     * @param signCertByte 证书
     */
    // private boolean isMcscaCert(byte[] signCertByte) {
    //     if (permit(signCertByte)) {
    //         return false;
    //     }
    //     //获取当前证书的授权密钥标识符
    //     String authorityKeyIdent;
    //     try {
    //         authorityKeyIdent = CertUtil.parseCert(signCertByte, CertUtil.AUTHORITY_KEY_IDENTIFIER);
    //     } catch (Exception e) {
    //         throw new McscaServiceException(EsvExceptionEnum.ESV_40003);
    //     }
    //
    //     RootCertEntity rootCertEntity = rootCertDao.findBySubjectKeyIdentifier(authorityKeyIdent);
    //     if (ObjectUtil.isEmpty(rootCertEntity) || ObjectUtil.isNull(rootCertEntity)) {
    //         throw new McscaServiceException(EsvExceptionEnum.ESV_40002);
    //     }
    //     return rootCertEntity.getCaName().contains("大陆云盾") || "MCSCA".equals(rootCertEntity.getCaAbbreviation()) || rootCertEntity.getCertIssuer().toUpperCase().contains("MCSCA");
    // }

    /**
     * 验证签章证书的crl
     *
     * @param certSn 证书序列号
     * @return 验证结果
     */
    private static boolean signCertVerifyCrl(String certSn) {
        CertQueryResponse certQueryResponse = certQuery(certSn);
        return certQueryResponse.getCertQueries().size() != 0;
    }


    /**
     * 放开验证逻辑
     *
     * @param certByte
     * @return
     */
    // public boolean permit(byte[] certByte) {
    //     List<String> permitAll = new ArrayList<>();
    //     permitAll.add("abe1b1e76deaa6d438361b558f191a95663d7696");
    //     //获取证书的使用密钥标识符
    //     String AUTHORITY_KEY_IDENTIFIER;
    //     try {
    //         AUTHORITY_KEY_IDENTIFIER = CertUtil.parseCert(certByte, CertUtil.AUTHORITY_KEY_IDENTIFIER);
    //     } catch (Exception e) {
    //         //针对用户证书没有授权者密钥,通过主题项判断
    //         String certIssuer;
    //         try {
    //             certIssuer = CertUtil.parseCert(certByte, CertUtil.ISSUER);
    //         } catch (Exception exception) {
    //             throw new McscaServiceException(EsvExceptionEnum.ESV_40005);
    //         }
    //         RootCertEntity rootCert = rootCertDao.findByCertSubject(certIssuer);
    //         if (ObjectUtil.isEmpty(rootCert) || ObjectUtil.isNull(rootCert)) {
    //             throw new McscaServiceException(EsvExceptionEnum.ESV_40002);
    //         } else {
    //             try {
    //                 return CertUtil.verifyCertificate(certByte, Base64.decode(rootCert.getCertBase64()));
    //             } catch (Exception e1) {
    //                 throw new McscaServiceException(EsvExceptionEnum.ESV_40004);
    //             }
    //         }
    //     }
    //     return permitAll.contains(AUTHORITY_KEY_IDENTIFIER);
    // }


    /**
     * 对接证书一级：获取证书的吊销时间
     *
     * @param certSn 序列号
     */
    private static CertQueryResponse certQuery(String certSn) {
        //初始化配置接口调用地址、应用ID、签名私钥
        Config config = new Config(endpoint, appId, pri);
        //构建连接客户端实例
        CmsClient cmsClient = new CmsClient(config);
        // 组装请求参数-证书查询
        CertQueryRequest certQueryRequest = new CertQueryRequest();
        // 订单ID和证书序列号必传一个
        certQueryRequest.setSn(certSn);
        // 设置Token认证令牌
        certQueryRequest.setToken(getToken());
        //业务流水号大于等于20位小于64位
        certQueryRequest.setSerialNo(RandomUtil.randomNumbers(20));
        CertQueryResponse certQueryResponse;
        try {
            certQueryResponse = cmsClient.execute(certQueryRequest);
        } catch (Exception e) {
            throw new IllegalArgumentException("【CMS】证书管理系统链接失败");
        }
        // 当code等于2000时表示调用成功
        if (StrUtil.equalsAnyIgnoreCase(certQueryResponse.getCode(), "2000", "success")) {
            if (CollUtil.isEmpty(certQueryResponse.getCertQueries())) {
                throw new IllegalArgumentException("【CMS】证书管理系统无此证书信息");
            }
            return certQueryResponse;
        } else {
            throw new IllegalArgumentException("【CMS】证书管理系统获取证书信息失败");
        }
    }

    /**
     * 对接证书一级：获取token
     *
     * @return token
     */
    private static String getToken() {
        //初始化配置接口调用地址、应用ID、签名私钥
        Config config = new Config(endpoint, appId, pri);
        //构建连接客户端实例
        CmsClient cmsClient = new CmsClient(config);
        //组装请求对象
        TokenRequest tokenRequest = new TokenRequest();
        //必填：大陆云盾提供
        tokenRequest.setAppId(appId);
        //必填：大陆云盾提供
        tokenRequest.setAppSecret(appSecret);
        //业务流水号  大于等于20位，小于64位
        tokenRequest.setSerialNo(RandomUtil.randomNumbers(20));
        //发送服务请求，接收响应参数
        TokenResponse tokenResponse;
        try {
            tokenResponse = cmsClient.execute(tokenRequest);
        } catch (Exception e) {
            throw new IllegalArgumentException("【CMS】证书管理系统链接失败");
        }
        String token;
        if (StrUtil.equalsAnyIgnoreCase(tokenResponse.getCode(), "2000", "success")) {
            token = tokenResponse.getToken();
            return token;
        } else {
            throw new IllegalArgumentException("【CMS】证书管理系统获取token失败");
        }
    }
}
