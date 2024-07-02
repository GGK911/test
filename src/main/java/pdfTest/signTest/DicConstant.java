package pdfTest.signTest;

public interface DicConstant {

    /**
     * Request Attribute key
     */
    interface ATTRIBUTE {
        //日志对象
        String LOG_ENTITY = "logEntity";
        //请求ID
        String REQ_ID = "reqId";
        //响应Code
        String RES_CODE = "resCode";
        //随机数
        String RANDOM_NUMBER = "randomNumber";
    }

    /**
     * 请求路径URL
     */
    interface REQ_URL {
        //个人开户
        String USER_CREATE_PERSONAL = "/busi/user/createPersonal";
        //企业开户
        String USER_CREATE_ENTERPRISE = "/busi/user/createEnterprise";
        //更新用户
        String USER_UPDATE = "/busi/user/updateEnterprise";
        //查询用户
        String USER_CHECK = "/busi/user/checkUser";
        //获取证书
        String CERT_GET_INFO = "/busi/cert/getCertInfo";
        //注销证书
        String CERT_REVOKE = "/busi/cert/revoke";
        //创建印章
        String SEAL_CREATE = "/busi/seal/create";
        //获取印章
        String SEAL_GET_INFO = "/busi/seal/getSealInfo";
        //获取印章Base64
        String SEAL_GET_BASE64 = "/busi/seal/getBase64";
        //上传合同
        String CONTRACT_UPLOAD = "/busi/contract/upload";
        //服务端签署合同
        String SERVER_SIGN = "/busi/sign/server";
        //服务端签署合同
        String SERVER_SIGN_UPDATE = "/busi/sign/serverUpdate";
        //服务端生成待签名数据
        String CREATE_PKCS7 = "/busi/sign/createPkcs7";
        //签名待签名数据
        String PKCS7_SIGN = "/busi/sign/signData";
        //客户端签署合同
        String CLIENT_SIGN = "/busi/sign/signFile";
        //下载合同
        String SIGN_DOWNLOAD = "/busi/sign/download";
        //获取签署时间
        String GET_SIGN_DATE = "/busi/sign/getSignDate";
        //验签合同
        String VERIFY_SIGN = "/busi/sign/verify";
        //验证Pkcs7
        String VERIFY_PKCS7 = "/busi/random/verifyPkcs7";
        //短信发送
        String SMS_SEND = "/busi/sms/send";
        //短信验证码校验
        String SMS_CHECK = "/busi/sms/check";

    }

    /**
     * 用户类型
     */
    interface BUSI_USER_TYPE {
        //个人用户
        Long PERSONAL = 1L;
        //企业用户
        Long ENTERPRISE = 2L;
    }

    /**
     * 用户状态
     */
    interface BUSI_USER_STATUS {
        //正常
        Long NORMAL = 1L;
        //注销
        Long CANCELLED = 0L;
    }

    /**
     * 证书周期
     */
    interface BUSI_CERT_CYCLE {
        //事件证书
        String EVENT = "1";
        //一年证书
        String LONG = "365";
    }

    /**
     * 证书类型
     */
//    interface BUSI_CERT_TYPE {
//        // 个人
//        Long PERSONAL = 1L;
//        // 企业
//        Long ENTERPRISE = 2L;
//    }

    /**
     * 证书算法
     */
    interface BUSI_CERT_TYPE {
        // RSA
        String RSA = "RSA2048";
        // SM2
        String SM2 = "SM2256";
    }

    /**
     * 证书状态
     */
    interface BUSI_CERT_STATUS {
        //正常
        Long NORMAL = 1L;
        //过期
        Long EXPIRED = 0L;
        //注销
        Long REVOKE = 2L;
    }

    /**
     * 印章类型
     */
    interface BUSI_SEAL_TYPE {
        //企业印章
        Long ENTERPRISE = 1L;
        //个人印章
        Long PERSONAL = 2L;
        //日期印章
        Long DATE = 3L;
    }

    /**
     * 印章来源
     */
    interface BUSI_SEAL_RESOURCE {
        //自动生成
        String CREATE = "1";
        //上传
        String UPLOAD = "2";
    }

    /**
     * 印章状态
     */
    interface BUSI_SEAL_STATUS {
        //正常
        Long NORMAL = 1L;
        //停用
        Long DISABLED = 0L;
    }

    /**
     * 合同模板状态
     */
    interface BUSI_CONTRACT_STATUS {
        //正常
        Long NORMAL = 1L;
        //删除
        Long DELETE= 0L;
    }

    interface BUSI_FILE_TYPE{
        String OFD = "zip";
        String PDF = "pdf";
    }

    /**
     * 签署方式
     */
    interface BUSI_SIGN_TYPE {
        //服务端签署
        String SERVER = "1";
        //客户端签署
        String CLIENT = "2";
    }

    /**
     * 签署方式
     */
    interface BUSI_SIGN_WAY {
        //坐标签署
        String COORDINATE = "1";
        //关键字签署
        String KEYWORD = "2";
        //签署域
        String SIGNAREA = "3";
    }

    /**
     * 签署记录状态
     */
    interface BUSI_SIGN_STATUS {
        //正常
        Long NORMAL = 1L;
        //停用
        Long DISABLED = 0L;
    }

    /**
     * 日志是否归档
     */
    interface BUSI_LOG_IS_FILING {
        //是
        boolean YES = true;
        //否
        boolean NO = false;
    }

    /**
     * 短信验证码状态
     */
    interface BUSI_SMS_STATUS {
        //有效
        Long NORMAL = 1L;
        //已验证
        Long VALIDATE = 2L;
        //已过期
        Long EXPIRED = 0L;
    }
}
