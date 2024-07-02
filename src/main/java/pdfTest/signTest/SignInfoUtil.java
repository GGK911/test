package pdfTest.signTest;

import cn.com.mcsca.bouncycastle.asn1.ASN1ObjectIdentifier;
import cn.com.mcsca.bouncycastle.asn1.ASN1OctetString;
import cn.com.mcsca.bouncycastle.asn1.DERBitString;
import cn.com.mcsca.bouncycastle.asn1.DEROctetString;
import cn.com.mcsca.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import cn.com.mcsca.extend.SecuEngine;
import cn.com.mcsca.gm.seal.SESeal;
import cn.com.mcsca.gm.seal.SESealBuilder;
import cn.com.mcsca.gm.seal.SESealInfo;
import cn.com.mcsca.itextpdf.text.pdf.AcroFields;
import cn.com.mcsca.pdf.signature.PdfKeywordFinder;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignInfoUtil {

    private static final Log logger = LogFactory.get();

    /**
     * 解析签署坐标
     *
     * @param coordinate 签署坐标
     * @return 签署坐标集合
     */
    public static List<float[]> analysisCoordinate(String coordinate) {
        // 签署坐标
        List<SignLocation> signLocations;
        List<float[]> positions = new ArrayList<>();
        try {
            signLocations = JSONUtil.parseArray(coordinate).toList(SignLocation.class);
            if (signLocations.isEmpty()) {
                throw new RuntimeException("invalid coordinate parameter");
            } else {
                signLocations.forEach(SignLocation::validate);
            }
        } catch (Exception e) {
            JSONObject params = JSONUtil.createObj();
            params.put("coordinate", coordinate);
            throw new RuntimeException();
        }
        for (SignLocation signLocation : signLocations) {
            int page = signLocation.getPage();
            float lbx = Convert.toFloat(signLocation.getLbx());
            float lby = Convert.toFloat(signLocation.getLby());
            float rtx = Convert.toFloat(signLocation.getRtx());
            float rty = Convert.toFloat(signLocation.getRty());
            //封装签署信息
            float[] positionInfo = {page, lbx, lby, rtx, rty};
            positions.add(positionInfo);
        }
        return positions;
    }


    /**
     * 通过关键字计算签署坐标
     *
     * @param keyWord       关键字
     * @param contractBytes 文件字节数组
     * @return 签署坐标集合
     */
    public static List<float[]> findByKeyWord(String keyWord, byte[] contractBytes, long userType) {
        List<float[]> keywordPositions;
        try {
            if (DicConstant.BUSI_USER_TYPE.PERSONAL.equals(userType)) {
                keywordPositions = PdfKeywordFinder.findKeywordPositions(contractBytes, keyWord, 45, 45);
            } else {
                keywordPositions = PdfKeywordFinder.findKeywordPositions(contractBytes, keyWord, 135, 135);
            }
//            keywordPositions = PdfKeywordFinder.findKeywordPositions(contractBytes, keyWord, 100, 100);
        } catch (Exception e) {
            JSONObject params = JSONUtil.createObj();
            params.put("keyWord", keyWord);
            throw new RuntimeException(e.getMessage());
        }
        if (keywordPositions == null || keywordPositions.size() == 0) {
            JSONObject params = JSONUtil.createObj();
            params.put("keyWord", keyWord);
            throw new RuntimeException();
        }

        //封装签署信息
        return new ArrayList<>(keywordPositions);
    }

    /**
     * 通过签署域计算签署坐标及生成待签署文件
     *
     * @param signAreaKey  signAreaKey
     * @param signPosition signPosition
     * @return
     */
    public static List<float[]> findBySignArea(String signAreaKey, Map<String, Object> signPosition) {
        List<float[]> positions = new ArrayList<>();
        // 文件模板生成待签署文件

        AcroFields.FieldPosition signAreaPosition = (AcroFields.FieldPosition) signPosition.get(signAreaKey);
        if (signAreaPosition != null) {
            int page = signAreaPosition.page;
            float lbx = signAreaPosition.position.getLeft();
            float lby = signAreaPosition.position.getBottom();
            float rtx = signAreaPosition.position.getRight();
            float rty = signAreaPosition.position.getTop();
            // 签署宽度除2
//            int w = sealConfig.getSignWidth() / 2;
//            int h = sealConfig.getSignHeight() / 2;
//            float centerX = (rtx - lbx) / 2 + lbx;
//            float centerY = (rty - lby) / 2 + lby;
//            lbx = centerX - w;
//            lby = centerY - h;
//            rtx = centerX + w;
//            rty = centerY + h;
            // 签署域大小进行签署
            float[] positionInfo = {page, lbx, lby, rtx, rty};
            positions.add(positionInfo);
        }
        return positions;
    }

    /**
     * 生成SESeal结构体
     *
     * @param sealBytes 印章数组
     * @param pubKey    签章者&制章者证书公钥
     * @param priKey    制章者私钥
     * @param name      名称
     * @return
     */
    public static String createSESeal(byte[] sealBytes, String pubKey, String priKey, String name) {
        List<String> certInfoList = new ArrayList<>();
        certInfoList.add(pubKey);
        DateTime now = DateTime.now();
        DateTime start = DateTime.now();
        DateTime end = start.offsetNew(DateField.YEAR, 1);
        SESealBuilder builder = SESealBuilder.create()
                .vid("MCSCA")
                .esID(RandomUtil.randomString(32))
                .type(1L)
                .name(name)
                .certListType(1L)
                .certInfoList(certInfoList)
                .createDate(now)
                .validStart(start)
                .validEnd(end)
                .picture(sealBytes)
                .build();
        String seSealBase64;
        try {
            SESealInfo eSealInfo = builder.getSESealInfo();
            ASN1OctetString cert = new DEROctetString(Base64.decode(pubKey));
            String signedValueString = new SecuEngine().SignDataBySM2(priKey, eSealInfo.getEncoded());
            DERBitString signedValue = new DERBitString(Base64.decode(signedValueString));
            ASN1ObjectIdentifier signAlgID = OIWObjectIdentifiers.sm3WithSM2;
            SESeal seSeal = new SESeal(eSealInfo, cert, signAlgID, signedValue);
            seSealBase64 = cn.com.mcsca.bouncycastle.util.encoders.Base64.toBase64String(seSeal.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return seSealBase64;
    }

}

