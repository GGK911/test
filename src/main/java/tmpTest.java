import certTest.createCert.PemUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/2 9:54
 */
public class tmpTest {
    @Test
    @SneakyThrows
    public void strToFile() {
        Path path = Paths.get("target/DAECCKey.dat");

        String str = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000107002a0b30e0d8a25284cfbaed1aac287b13ad9d80271d9c03ef040230dac34e6efc08dc1a03c87dd13c0583ec531ba14964dafa8a3623b4b4f3ced2bb77ee73b8645547422b780b1375c44b2c87bcea3c02b436113e286222651287fad2a230f358";
        FileUtil.writeBytes(Hex.decode(str), path.toAbsolutePath().toString());
    }

    @Test
    @SneakyThrows
    public void cutFile() {
        byte[] bytes = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\test02.pdf");
        byte[] content = new byte[268328 - 247846];
        System.arraycopy(bytes, 247846, content, 0, content.length);
        System.out.println(new String(content, StandardCharsets.UTF_8));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class Extension {
        String extName;
        String extOID;
        String extValue;
    }

    @Test
    @SneakyThrows
    public void mapJsonTest() {
        Extension extension1 = new Extension("客户自定义的扩展", "1.2.3.4.5.6.7.8.9.0", "test1");
        Extension extension2 = new Extension("客户自定义的扩展", "1.2.3.4.5.6.7.8.9.0", "test2");
        Extension extension3 = new Extension("客户自定义的扩展", "1.2.3.4.5.6.7.8.9.0", "test3");
        List<Extension> list = Arrays.asList(extension1, extension2, extension3);
        String listJsonStr = JSONUtil.toJsonStr(list);
        System.out.println(String.format("%-16s", "listJsonStr>> ") + listJsonStr);
    }

    @Test
    @SneakyThrows
    public void mapJsonTest2() {
        TreeMap<String, Object> certExts = new TreeMap<>();
        certExts.put("1.2.3", "test1");
        String listJsonStr = JSONUtil.toJsonStr(certExts);
        System.out.println(String.format("%-16s", "listJsonStr>> ") + listJsonStr);
    }

    @Test
    @SneakyThrows
    public void stringTest() {
        String certDn = "CN=TperRSAx1@唐好凯@01@005";
        String key = "";
        String[] certDns = certDn.split(",");
        int i = 0;
        String cn = "";
        StringBuilder stringBuffer = new StringBuilder();
        for (String certSubject : certDns) {
            if (i > 0) {
                stringBuffer.append(",");
            }
            if (StringUtils.isEmpty(cn)) {
                String[] cert = certSubject.split("=");
                if (cert.length > 0 && "CN".equals(cert[0])) {
                    if (!StringUtils.isEmpty(cert[1]) && cert[1].lastIndexOf("@") != -1) {
                        String numberCount = cert[1].substring(cert[1].lastIndexOf("@") + 1);
                        if (!StringUtils.isEmpty(cert[1]) && StringUtils.isNumeric(numberCount)) {
                            // thk's todo 2024/8/28 17:09 这里的dbIndex是真的吗？不根据配置文件来？？
                            Long raCertCountNumber = 6L;
                            String addNumber = String.valueOf(raCertCountNumber);
                            stringBuffer.append(cert[0]).append("=").append(cert[1], 0, cert[1].lastIndexOf("@")).append("@");
                            for (int q = 0, b = 3 - addNumber.length(); q < b; q++) {
                                stringBuffer.append("0");
                            }
                            stringBuffer.append(addNumber);

                            cn = stringBuffer.toString();
                        }
                    }
                } else {
                    stringBuffer.append(certSubject);
                }
            } else {
                stringBuffer.append(certSubject);
            }
            i++;
        }
        System.out.println(String.format("%-16s", "stringBuffer>> ") + stringBuffer);
    }

    @Test
    @SneakyThrows
    public void subjectAltNamesTest() {
        // 使用者可选名称
        GeneralName[] subjectAltNames = new GeneralName[]{
                new GeneralName(GeneralName.dNSName, "www.msca.com"),
                new GeneralName(GeneralName.dNSName, "mcsca.com"),
                new GeneralName(GeneralName.dNSName, "msca.com"),
                new GeneralName(GeneralName.dNSName, "*.msca.com"),
                new GeneralName(GeneralName.dNSName, "*.mcsca.com"),
                new GeneralName(GeneralName.dNSName, "www.mcsca.com")
        };
        GeneralNames asn1Encodable = new GeneralNames(subjectAltNames);
        System.out.println(Hex.toHexString(asn1Encodable.getEncoded()));
    }

    @Test
    @SneakyThrows
    public void StringTest() {
        ASN1Sequence instance = ASN1Sequence.getInstance(Hex.decode("302C820D7777772E62616964752E636F6D820C7777772E6D7363612E636F6D820D7777772E6D637363612E636F6D"));
        for (int i = 0; i < instance.size(); i++) {
            DLTaggedObject objectAt = (DLTaggedObject) instance.getObjectAt(i);
            DEROctetString baseObject = (DEROctetString) objectAt.getBaseObject();
            System.out.println(new String(baseObject.getOctets(), StandardCharsets.UTF_8));
        }

        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new GeneralName(2, "www.msca.com"));
        v.add(new GeneralName(2, "www.mcsca.com"));
        DERSequence derSeq = new DERSequence(v);
        System.out.println(Hex.toHexString(derSeq.getEncoded()));
    }

    @Test
    @SneakyThrows
    public void base64Test() {
        String cfcaBase64Str = "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";
        cfcaBase64Str = "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";

        byte[] dataBytes = Base64.decode(cfcaBase64Str);
        System.out.println(Hex.toHexString(dataBytes));

        // HEAD 包括 2 个字节的版本号（version）、
        // 2 字节的头部长度信息（headLength）、
        // 2字节浏览器信息长度（explorerInfoLength）、
        // 浏览器信息（explorerInfo）、
        // 2 字节操作系统信息长度（platformInfoLength）、
        // 操作系统信息（platformInfo）、
        // 2 字节笔画的个数 （ strokeCount ）、
        // 手 写 开 始 时 的 时 间 （ startTime ）、
        // 2 字 节 扩 展 字 段 长 度（extensionLength）、
        // 扩展字段（extension）。扩展字段（extension）后面可以根据需要增加字段，如画布的宽(canvasWidth)和画布的高(canvasHeight)，解析这些新增字段时要注意其有可能为空。
        byte[] HEAD_VERSION = new byte[2];
        System.arraycopy(dataBytes, 0, HEAD_VERSION, 0, HEAD_VERSION.length);
        int headVersion = bytesToInt(HEAD_VERSION);
        System.out.println(String.format("%-16s", "版本号headVersion>> ") + headVersion);
        System.out.println(Hex.toHexString(HEAD_VERSION));

        byte[] HEAD_LENGTH = new byte[2];
        System.arraycopy(dataBytes, HEAD_VERSION.length, HEAD_LENGTH, 0, HEAD_LENGTH.length);
        int headLength = bytesToInt(HEAD_LENGTH);
        System.out.println(String.format("%-16s", "头部长度headLength>> ") + headLength);
        System.out.println(Hex.toHexString(HEAD_LENGTH));

        byte[] HEAD_EXPLORER_INFO_LENGTH = new byte[2];
        System.arraycopy(dataBytes, HEAD_VERSION.length + HEAD_VERSION.length, HEAD_EXPLORER_INFO_LENGTH, 0, HEAD_EXPLORER_INFO_LENGTH.length);
        int explorerInfoLength = bytesToInt(HEAD_EXPLORER_INFO_LENGTH);
        System.out.println(String.format("%-16s", "浏览器信息长度explorerInfoLength>> ") + explorerInfoLength);
        System.out.println(Hex.toHexString(HEAD_EXPLORER_INFO_LENGTH));

        byte[] HEAD_EXPLORER_INFO = new byte[explorerInfoLength];
        System.arraycopy(dataBytes, HEAD_VERSION.length + HEAD_LENGTH.length + HEAD_EXPLORER_INFO_LENGTH.length, HEAD_EXPLORER_INFO, 0, HEAD_EXPLORER_INFO.length);
        String explorerInfo = new String(HEAD_EXPLORER_INFO, StandardCharsets.UTF_8);
        System.out.println(String.format("%-16s", "浏览器信息explorerInfo>> ") + explorerInfo);
        System.out.println(Hex.toHexString(HEAD_EXPLORER_INFO));

        byte[] HEAD_PLATFORM_INFO_LENGTH = new byte[2];
        System.arraycopy(dataBytes, HEAD_VERSION.length + HEAD_LENGTH.length + HEAD_EXPLORER_INFO_LENGTH.length + HEAD_EXPLORER_INFO.length, HEAD_PLATFORM_INFO_LENGTH, 0, HEAD_PLATFORM_INFO_LENGTH.length);
        int headPlatformInfoLength = bytesToInt(HEAD_PLATFORM_INFO_LENGTH);
        System.out.println(String.format("%-16s", "操作系统信息长度headPlatformInfoLength>> ") + headPlatformInfoLength);
        System.out.println(Hex.toHexString(HEAD_PLATFORM_INFO_LENGTH));

        byte[] HEAD_PLATFORM_INFO = new byte[headPlatformInfoLength];
        System.arraycopy(dataBytes, HEAD_VERSION.length + HEAD_LENGTH.length + HEAD_EXPLORER_INFO_LENGTH.length + HEAD_EXPLORER_INFO.length + HEAD_PLATFORM_INFO_LENGTH.length, HEAD_PLATFORM_INFO, 0, HEAD_PLATFORM_INFO.length);
        String headPlatformInfo = new String(HEAD_PLATFORM_INFO, StandardCharsets.UTF_8);
        System.out.println(String.format("%-16s", "操作系统信息headPlatformInfo>> ") + headPlatformInfo);
        System.out.println(Hex.toHexString(HEAD_PLATFORM_INFO));

        byte[] HEAD_STROKE_COUNT = new byte[2];
        System.arraycopy(dataBytes, HEAD_VERSION.length + HEAD_LENGTH.length + HEAD_EXPLORER_INFO_LENGTH.length + HEAD_EXPLORER_INFO.length + HEAD_PLATFORM_INFO_LENGTH.length + HEAD_PLATFORM_INFO.length, HEAD_STROKE_COUNT, 0, HEAD_STROKE_COUNT.length);
        int headStrokeCount = bytesToInt(HEAD_STROKE_COUNT);
        System.out.println(String.format("%-16s", "笔画的个数headStrokeCount>> ") + headStrokeCount);
        System.out.println(Hex.toHexString(HEAD_STROKE_COUNT));

        // thk's todo 2024/11/12 10:41 猜测时间字节 得出结果为6字节
        byte[] HEAD_START_TIME_6 = new byte[6];
        System.arraycopy(dataBytes, HEAD_VERSION.length + HEAD_LENGTH.length + HEAD_EXPLORER_INFO_LENGTH.length + HEAD_EXPLORER_INFO.length + HEAD_PLATFORM_INFO_LENGTH.length + HEAD_PLATFORM_INFO.length + HEAD_STROKE_COUNT.length, HEAD_START_TIME_6, 0, HEAD_START_TIME_6.length);
        Date date = parse6ByteHexToDate(Hex.toHexString(HEAD_START_TIME_6), true);
        System.out.println("手写开始时间>> " + DateUtil.format(date, DatePattern.CHINESE_DATE_TIME_FORMAT));
        System.out.println(Hex.toHexString(HEAD_START_TIME_6));

        // 是8的话越界了 测试 2 4 6 8 居然只有6能通过
        byte[] HEAD_EXTENSION_LENGTH = new byte[2];
        System.arraycopy(dataBytes, HEAD_VERSION.length + HEAD_LENGTH.length + HEAD_EXPLORER_INFO_LENGTH.length + HEAD_EXPLORER_INFO.length + HEAD_PLATFORM_INFO_LENGTH.length + HEAD_PLATFORM_INFO.length + HEAD_STROKE_COUNT.length + 6, HEAD_EXTENSION_LENGTH, 0, HEAD_EXTENSION_LENGTH.length);
        int headExtensionLength = bytesToInt(HEAD_EXTENSION_LENGTH);
        System.out.println(String.format("%-16s", "扩展字段长度headExtensionLength>> ") + headExtensionLength);
        System.out.println(Hex.toHexString(HEAD_EXTENSION_LENGTH));

        byte[] HEAD_EXTENSION = new byte[headExtensionLength];
        System.arraycopy(dataBytes, HEAD_VERSION.length + HEAD_LENGTH.length + HEAD_EXPLORER_INFO_LENGTH.length + HEAD_EXPLORER_INFO.length + HEAD_PLATFORM_INFO_LENGTH.length + HEAD_PLATFORM_INFO.length + HEAD_STROKE_COUNT.length + HEAD_START_TIME_6.length + HEAD_EXTENSION_LENGTH.length, HEAD_EXTENSION, 0, HEAD_EXTENSION.length);
        String headExtension = new String(HEAD_EXTENSION, StandardCharsets.UTF_8);
        System.out.println(String.format("%-16s", "扩展字段headExtension>> ") + headExtension);
        System.out.println(Hex.toHexString(HEAD_EXTENSION));

        int headCountLength = HEAD_VERSION.length + HEAD_LENGTH.length + HEAD_EXPLORER_INFO_LENGTH.length + HEAD_EXPLORER_INFO.length + HEAD_PLATFORM_INFO_LENGTH.length + HEAD_PLATFORM_INFO.length + HEAD_STROKE_COUNT.length + HEAD_START_TIME_6.length + HEAD_EXTENSION_LENGTH.length;
        System.out.println(String.format("%-16s", "headCountLength>> ") + headCountLength);

        // 对 于 每 一 笔 笔 画 ， 其 STROKE HEAD 数据包括
        // 1 字 节 笔 画 头 部 长 度（strokeHeadLength）、
        // 1 字节点的特征信息长度（featureInfoLength）、
        // 点的特征信息（featureInfo）、
        // 3 字节笔画开始时间（strokeTime）、
        // 3 字节笔画颜色（strokeColor）、
        // 1 字节该笔画包含的点的个数字段长度（pointsCountLength）、
        // 该笔画包含的点的个数（pointsCount）、
        // 4 字节最小笔画粗细（strokePenMinSize）、
        // 4 字节最大笔画粗细（strokePenMaxSize）。
        // 最后存储点的特征，先存储一笔所有点的 x 坐标，然后是 y 坐标，最后是时间 t 信息。
        int point1 = headCountLength;
        // while (dataBytes.length > point1)
        {
            byte[] STROKE_HEAD_LENGTH = new byte[1];
            System.arraycopy(dataBytes, headCountLength, STROKE_HEAD_LENGTH, 0, STROKE_HEAD_LENGTH.length);
            int strokeHeadLength = convertByteToInt(STROKE_HEAD_LENGTH);
            System.out.println(String.format("%-16s", "strokeHeadLength>> ") + strokeHeadLength);
            System.out.println(Hex.toHexString(STROKE_HEAD_LENGTH));

            byte[] STROKE_HEAD = new byte[strokeHeadLength];
            System.arraycopy(dataBytes, headCountLength, STROKE_HEAD, 0, STROKE_HEAD.length);
            String strokeHeadHex = Hex.toHexString(STROKE_HEAD);
            System.out.println(String.format("%-16s", "strokeHeadHex>> ") + strokeHeadHex);

            byte[] STROKE_HEAD_FEATURE_INFO_LENGTH = new byte[1];
            System.arraycopy(dataBytes, headCountLength + STROKE_HEAD_LENGTH.length, STROKE_HEAD_FEATURE_INFO_LENGTH, 0, STROKE_HEAD_FEATURE_INFO_LENGTH.length);
            int strokeHeadFeatureInfoLength = convertByteToInt(STROKE_HEAD_FEATURE_INFO_LENGTH);
            System.out.println(String.format("%-16s", "strokeHeadFeatureInfoLength>> ") + strokeHeadFeatureInfoLength);
            System.out.println(Hex.toHexString(STROKE_HEAD_FEATURE_INFO_LENGTH));

            byte[] STROKE_HEAD_FEATURE_INFO = new byte[strokeHeadFeatureInfoLength];
            System.arraycopy(dataBytes, headCountLength + STROKE_HEAD_LENGTH.length + STROKE_HEAD_FEATURE_INFO_LENGTH.length, STROKE_HEAD_FEATURE_INFO, 0, STROKE_HEAD_FEATURE_INFO.length);
            String strokeHeadFeatureInfo = new String(STROKE_HEAD_FEATURE_INFO, StandardCharsets.UTF_8);
            System.out.println(String.format("%-16s", "strokeHeadFeatureInfo>> ") + strokeHeadFeatureInfo);
            System.out.println(Hex.toHexString(STROKE_HEAD_FEATURE_INFO));


            // thk's todo 2024/11/12 11:17 这个时间好像不对
            byte[] STROKE_TIME = new byte[3];
            System.arraycopy(dataBytes, headCountLength + STROKE_HEAD_LENGTH.length + STROKE_HEAD_FEATURE_INFO_LENGTH.length + STROKE_HEAD_FEATURE_INFO.length, STROKE_TIME, 0, STROKE_TIME.length);
            Date date1 = parse3ByteTimestamp(STROKE_TIME, false);
            System.out.println(DateUtil.format(date1, DatePattern.CHINESE_DATE_TIME_FORMAT));
            System.out.println(Hex.toHexString(STROKE_TIME));

            byte[] STROKE_COLOR = new byte[3];
            System.arraycopy(dataBytes, headCountLength + STROKE_HEAD_LENGTH.length + STROKE_HEAD_FEATURE_INFO_LENGTH.length + STROKE_HEAD_FEATURE_INFO.length + STROKE_TIME.length, STROKE_COLOR, 0, STROKE_COLOR.length);
            System.out.println(Hex.toHexString(STROKE_COLOR));

            byte[] STROKE_POINTS_COUNT_LENGTH = new byte[1];
            System.arraycopy(dataBytes, headCountLength + STROKE_HEAD_LENGTH.length + STROKE_HEAD_FEATURE_INFO_LENGTH.length + STROKE_HEAD_FEATURE_INFO.length + STROKE_TIME.length + STROKE_COLOR.length, STROKE_POINTS_COUNT_LENGTH, 0, STROKE_POINTS_COUNT_LENGTH.length);
            int strokePointsCountLength = convertByteToInt(STROKE_POINTS_COUNT_LENGTH);
            System.out.println(String.format("%-16s", "strokePointsCountLength>> ") + strokePointsCountLength);
            System.out.println(Hex.toHexString(STROKE_POINTS_COUNT_LENGTH));

            byte[] STROKE_POINTS_COUNT = new byte[strokePointsCountLength];
            System.arraycopy(dataBytes, headCountLength + STROKE_HEAD_LENGTH.length + STROKE_HEAD_FEATURE_INFO_LENGTH.length + STROKE_HEAD_FEATURE_INFO.length + STROKE_TIME.length + STROKE_COLOR.length + STROKE_POINTS_COUNT_LENGTH.length, STROKE_POINTS_COUNT, 0, STROKE_POINTS_COUNT.length);
            long strokePointsCount = Long.parseUnsignedLong(Hex.toHexString(STROKE_POINTS_COUNT), 16);
            System.out.println(String.format("%-16s", "strokePointsCount>> ") + strokePointsCount);
            System.out.println(Hex.toHexString(STROKE_POINTS_COUNT));

            byte[] STROKE_PEN_MIN_SIZE = new byte[4];
            System.arraycopy(dataBytes, headCountLength + STROKE_HEAD_LENGTH.length + STROKE_HEAD_FEATURE_INFO_LENGTH.length + STROKE_HEAD_FEATURE_INFO.length + STROKE_TIME.length + STROKE_COLOR.length + STROKE_POINTS_COUNT_LENGTH.length + STROKE_POINTS_COUNT.length, STROKE_PEN_MIN_SIZE, 0, STROKE_PEN_MIN_SIZE.length);
            System.out.println(Hex.toHexString(STROKE_PEN_MIN_SIZE));

            byte[] STROKE_PEN_MAX_SIZE = new byte[4];
            System.arraycopy(dataBytes, headCountLength + STROKE_HEAD_LENGTH.length + STROKE_HEAD_FEATURE_INFO_LENGTH.length + STROKE_HEAD_FEATURE_INFO.length + STROKE_TIME.length + STROKE_COLOR.length + STROKE_POINTS_COUNT_LENGTH.length + STROKE_POINTS_COUNT.length + STROKE_PEN_MIN_SIZE.length, STROKE_PEN_MAX_SIZE, 0, STROKE_PEN_MAX_SIZE.length);
            System.out.println(Hex.toHexString(STROKE_PEN_MAX_SIZE));

            long xytLength = strokePointsCount * 2 * 3;
            System.out.println(String.format("%-16s", "xytLength>> ") + xytLength);

            byte[] STROKE_X_Y_T = new byte[(int) xytLength];
            System.arraycopy(dataBytes, headCountLength + strokeHeadLength, STROKE_X_Y_T, 0, STROKE_X_Y_T.length);
            System.out.println(Hex.toHexString(STROKE_X_Y_T));


        }


    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes == null || bytes.length != 2) {
            throw new IllegalArgumentException("Input byte array must be of length 2.");
        }
        return ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
    }

    public static int convertByteToInt(byte[] byteArray) {
        if (byteArray.length != 1) {
            throw new IllegalArgumentException("Input byte array must have exactly 1 byte.");
        }
        // 将1字节无符号转换为int
        return Byte.toUnsignedInt(byteArray[0]);
    }

    public static Date parse6ByteHexToDate(String hex, boolean asMilliseconds) {
        if (hex.length() < 12) {
            throw new IllegalArgumentException("Hex string must have at least 6 bytes (12 hex characters).");
        }

        // 截取前6字节（即12个十六进制字符）
        String hexTimestamp = hex.substring(0, 12);

        // 将6字节的16进制字符串解析为无符号整数
        long timestamp = Long.parseUnsignedLong(hexTimestamp, 16);

        // 根据指定的单位解析时间戳
        long timestampMillis;
        if (asMilliseconds) {
            // 如果为毫秒级，直接使用值
            timestampMillis = timestamp;
        } else {
            // 如果为秒级，将秒数转换为毫秒
            timestampMillis = timestamp * 1000;
        }

        // 转换为日期
        return new Date(timestampMillis);
    }

    public static Date parse3ByteTimestamp(byte[] byteArray, boolean asMilliseconds) {
        if (byteArray.length != 3) {
            throw new IllegalArgumentException("Input byte array must have exactly 3 bytes.");
        }

        // 将3字节拼接为一个24位无符号整数
        int timestamp = ((byteArray[0] & 0xFF) << 16) |
                ((byteArray[1] & 0xFF) << 8) |
                (byteArray[2] & 0xFF);

        // 根据指定的单位解析时间戳
        long timestampMillis;
        if (asMilliseconds) {
            // 如果为毫秒级，直接使用值
            timestampMillis = timestamp;
        } else {
            // 如果为秒级，将秒数转换为毫秒
            timestampMillis = (long) timestamp * 1000;
        }

        // 转换为日期
        return new Date(timestampMillis);
    }

    @Test
    @SneakyThrows
    public void tlvLengthTest() {
        String tlvStrBase64 = "MIICrjCCAlOgAwIBAgIQIjbre5T7qlVY/+AcwVjKMDAMBggqgRzPVQGDdQUAMC0xCzAJBgNVBAYTAkNOMQ4wDAYDVQQKDAVNQ1NDQTEOMAwGA1UEAwwFTUNTQ0EwHhcNMjQxMTA4MDkxOTI0WhcNMjUxMTA4MDkxOTI0WjBiMQswCQYDVQQGEwJDTjEjMCEGCSqGSIb3DQEJARYUemhhbmdzYW5AZXhhbXBsZS5jb20xLjAsBgNVBAMMJTEyMzQ1NkDlvKDkuIlAMDUyMjEyMTIwMDEwMzIxNjIxNkAwMDMwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAAQNUeI6+ybG01Rgm42xBWqPgjuwQ6bW5dFTCRZ9mvMamzpz4zCG0tTBpWBLVVjbB2/vOc3AKlwBQ5Dvpbu3zp8Go4IBHDCCARgwCwYDVR0PBAQDAgTwMAwGA1UdEwQFMAMBAQAwHwYDVR0jBBgwFoAU8SIKZ5iN9eOyqsMXa8BCH75LvXYwHQYDVR0OBBYEFOkMnFGqbMBY9LBtPehsA6NLbApoMIG6BgNVHR8EgbIwga8wLqAsoCqGKGh0dHA6Ly93d3cubWNzY2EuY29tLmNuL3NtMi9jcmwvY3JsMC5jcmwwfaB7oHmGd2xkYXA6Ly93d3cubWNzY2EuY29tLmNuOjM4OS9DTj1jcmwwLE9VPUNSTCxPPU1DU0NBLEM9Q04/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdD9iYXNlP29iamVjdGNsYXNzPWNSTERpc3RyaWJ1dGlvblBvaW50MAwGCCqBHM9VAYN1BQADRwAwRAIgGJ5Yg81IcxjSprN8TaRE7EcX/n2s80N7sJDM93HzbjsCIGR5njZbZd4QfM5TOq9mCtgiIpKBTIrGtxBRGmFjNp3a";
        byte[] tlvBytesa = Base64.decode(tlvStrBase64);
        int length = parseLength(tlvBytesa, 5);
        System.out.println(length);
    }

    public int parseLength(byte[] data, int offset) {
        int length = 0;
        int firstByte = data[offset] & 0xFF;

        if (firstByte < 128) { // 单字节长度
            length = firstByte;
        } else { // 多字节长度
            int numOfBytes = firstByte & 0x7F; // 获取后续字节数量
            for (int i = 1; i <= numOfBytes; i++) {
                length = (length << 8) | (data[offset + i] & 0xFF);
            }
        }
        return length;
    }

    @Test
    @SneakyThrows
    public void base64ToDerTest() {
        File dir = FileUtil.file("C:\\Users\\ggk911\\Desktop\\新根\\BASE64编码\\SM2证书链-NEW\\单独的根证书");
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                byte[] bytes = FileUtil.readBytes(file);
                byte[] decode = Base64.decode(bytes);
                FileUtil.writeBytes(decode, new File("C:\\Users\\ggk911\\Desktop\\新根\\DER编码二进制\\SM2证书链-NEW\\单独的根证书", file.getName()));
            }

        }
    }

    @Test
    @SneakyThrows
    public void derToBase64Test() {
        File dir = FileUtil.file("C:\\Users\\ggk911\\Desktop\\新根\\DER编码二进制\\SM2证书链-NEW");
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    byte[] bytes = FileUtil.readBytes(file);
                    byte[] encode = Base64.encode(bytes);
                    FileUtil.writeBytes(encode, new File("C:\\Users\\ggk911\\Desktop\\新根\\BASE64编码\\SM2证书链-NEW", file.getName()));
                }

            }
        }

    }

    @Test
    @SneakyThrows
    public void base64ToDerSingleFileTest() {
        String base64 = "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";
        FileUtil.writeBytes(Base64.decode(base64), "C:\\Users\\ggk911\\Desktop\\2.p7b");

    }

}
