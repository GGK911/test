package pdfTest.verifyReportTest;

import lombok.Data;

import java.util.ArrayList;

/**
 * 加强报告信息
 *
 * @author TangHaoKai
 * @version V1.0 2024/3/25 10:05
 */
@Data
public class ReportEntity {

    /**
     * 电子签名验证报告的唯一编码：验证主表的id
     */
    private String esvIndexId;

    /**
     * 申请主体：机构名称
     */
    private String orgName;

    /**
     * 文档格式：默认pdf
     */
    private String fileType;

    /**
     * 文档名称
     */
    private String fileName;

    /**
     * 杂凑算法：默认sha256
     */
    private String algType;
    /**
     * 杂凑值
     */
    private String fileHash;
    /**
     * 签章个数
     */
    private int signTotalNum;
    /**
     * 签章验证通过个数
     */
    private int signTrueNum;
    /**
     * 签章验证失败个数
     */
    private int signFalseNum;
    /**
     * 当前电子合同是否有过篡改记录
     */
    private boolean verifyModify;
    /**
     * 当前电子合同所有验证项是否验证通过
     */
    private boolean pass;
    /**
     * 是否只包含大陆云盾的根证书，不包含其他ca的根证书
     */
    private boolean onlyMcscaCert;
    /**
     * 签章详细信息
     */
    private ArrayList<ReportSignEntity> signInfoList;


    /**
     * 电子签名验证报告签章信息的实体类
     *
     * @author 文仔
     */
    @Data
    public static class ReportSignEntity {

        /**
         * 签名域名称
         */
        private String signatureName;

        /**
         * 签名域位置
         */
        private String signaturePage;

        /**
         * pdf签署时间：如果签章包含时间戳信息，那么签署时间和时间戳时间是一样的
         */
        private String signDate;

        /**
         * 签章图片
         */
        private byte[] sealData;

        /**
         * 签章数字证书主题
         */
        private String signCertSubject;

        /**
         * 签章数字证书序列号
         */
        private String signCertSn;

        /**
         * 签章数字证书颁发者
         */
        private String signCertIssuer;

        /**
         * 签章数字证书有效期-起始时间
         */
        private String signCertStartData;

        /**
         * 签章数字证书有效期-结束时间
         */
        private String signCertEndData;

        /**
         * 是否是mcsca的证书
         */
        private boolean mcscaCert;

        /**
         * 签章数字证书-跟证书验证结果
         */
        private boolean signCertVerifyRootCert;

        /**
         * 签章数字证书-crl验证结果
         */
        private boolean signCertVerifyCrl;

        /**
         * 签章时间是否在签章证书的有效期内
         */
        private boolean signDateVerifyBySignCert;

        /**
         * 签章是否包含时间戳
         */
        private boolean containTs;

        /**
         * 时间戳证书主题
         */
        private String tsCertSubject;

        /**
         * 时间戳证书序列号
         */
        private String tsCertSn;

        /**
         * 时间戳证书颁发者
         */
        private String tsCertIssuer;

        /**
         * 时间戳证书有效期-起始时间
         */
        private String tsCertStartData;

        /**
         * 时间戳证书有效期-结束时间
         */
        private String tsCertEndData;

        /**
         * 签章时间戳时间
         */
        private String tsSignDate;

        /**
         * 签章时间戳时间是否在时间戳证书的有效期内
         */
        private Boolean signDateVerifyByTsCert;

        /**
         * 是否是大陆云盾的时间戳
         */
        private Boolean mcscaTs;

        /**
         * 当前电子签章验证结果
         */
        private boolean verifyResult;

        /**
         * 当前签章之后是否被篡改
         */
        private boolean modify;

        //**********************************************合同的核验信息***************************************************//

        /**
         * 证书用户在合同类型（个人/企业）
         */
        private String userType;

        /**
         * 认证方式
         */
        private String authType;

        /**
         * 认证时间
         */
        private String authTime;

        /**
         * 认证结果
         */
        private String authResult;

        /**
         * 用户姓名
         */
        private String userName;

        /**
         * 用户证件类型
         */
        private String userCardType;

        /**
         * 用户证件号码
         */
        private String userCardNo;

        /**
         * 用户银行卡号码
         */
        private String userBankCardNo;

        /**
         * 用户手机号码
         */
        private String userPhone;

        /**
         * 认证短信验证码
         */
        private String authSmsCode;

        /**
         * 认证短信发送时间
         */
        private String authSmsSendTime;

        /**
         * 认证短信回填码
         */
        private String authSmsBackFillCode;

        /**
         * 认证短信回填时间
         */
        private String authSmsBackFillTime;

        /**
         * 法人姓名
         */
        private String legalPersonName;

        /**
         * 法人证件类型
         */
        private String legalPersonCardType;

        /**
         * 法人证件号码
         */
        private String legalPersonCardNo;

        /**
         * 法人手机号
         */
        private String legalPersonCardPhone;

        /**
         * 经办人姓名
         */
        private String agentPersonName;

        /**
         * 经办人证件类型
         */
        private String agentPersonCardType;

        /**
         * 经办人证件号码
         */
        private String agentPersonCardNo;

        /**
         * 经办人手机号
         */
        private String agentPersonCardPhone;

        /**
         * 回填打款金额
         */
        private String backFillPayMoney;

        /**
         * 回填打款时间
         */
        private String backFillPayTime;

        //**********************************************合同的证书及协议信息***************************************************//

        /**
         * 协议勾选时间
         */
        private String readProtocolEndTime;

        /**
         * 协议内容HASH（MD5）
         */
        private String readProtocolContent;

        //**********************************************合同的意愿认证信息***************************************************//

        /**
         * 意愿类型
         */
        private String willType;

        /**
         * 意愿时间
         */
        private String willTime;

        /**
         * 意愿发送短信验证码
         */
        private String willSmsCode;

        /**
         * 意愿短信发送时间
         */
        private String willSmsSendTime;

        /**
         * 意愿回填验证码
         */
        private String willSmsBackFillCode;

        /**
         * 意愿回填时间
         */
        private String willSmsBackFillTime;

        /**
         * 意愿结果
         */
        private String willResult;

    }
}
