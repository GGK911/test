package pdfTest;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * pdf
 * https://kb.itextpdf.com/home/it5kb/ebooks/the-best-itext-5-questions-on-stack-overflow
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-19 10:06
 **/
public class test {

    @SneakyThrows
    public static void main(String[] args) {

        System.out.println("//*************************************************读取PDF域参数**********************************************************//");

        String pdf = "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\合同测试模板.pdf";
        byte[] pdfBytes = FileUtil.readBytes(pdf);
        List<PdfParameterEntity> pdfParameterEntityList = new ArrayList<>();
        PdfUtil.getPdfDomain(pdfBytes, pdfParameterEntityList, null);
        System.out.println(JSONUtil.parse(pdfParameterEntityList).toStringPretty());

        System.out.println("//*************************************************PDF域填充**********************************************************//");

        // Map<String, String> map = new HashMap<>();
        // map.put("Check Box3", "是");
        // map.put("Text1", "1111111111111111111111");
        // map.put("Text2", "2222222222222222222222222222222222");
        //
        // byte[] pdfFill = PdfUtil.pdfFill(pdfBytes, map);
        // FileUtil.writeBytes(pdfFill, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\testFill.pdf");

        System.out.println("//*************************************************PDF域图片填充测试**********************************************************//");

        // PdfReader pdfReader;
        // try {
        //     pdfReader = new PdfReader(pdfBytes);
        // } catch (IOException e) {
        //     System.out.println("PdfReader异常");
        //     throw new RuntimeException("PdfReader异常");
        // }
        // ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // PdfStamper pdfStamper;
        // try {
        //     pdfStamper = new PdfStamper(pdfReader, bos);
        // } catch (DocumentException | IOException e) {
        //     System.out.println("PdfStamper异常");
        //     throw new RuntimeException("PdfStamper异常");
        // }
        // AcroFields acroFields = pdfStamper.getAcroFields();
        // if (acroFields.getFields().size() == 0) {
        //     System.out.println("无域信息");
        //     throw new RuntimeException("无域信息");
        // }
        //
        // // 图片
        // // byte[] pic = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\测试二维码.png");
        // byte[] pic = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\页眉log.png");
        // for (int i = 2; i < 7; i++) {
        //     PdfUtil.fillImage(pdfStamper, pic, acroFields, "Text" + i, false, 45D);
        // }
        //
        // // 如果为false那么生成的PDF文件还能编辑，一定要设为true
        // pdfStamper.setFormFlattening(true);
        // pdfStamper.close();
        // pdfReader.close();
        // FileUtil.writeBytes(bos.toByteArray(), "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\testPicFill.pdf");

        System.out.println("//*************************************************PDF域图片填充**********************************************************//");

        // 图片
        // byte[] pic = FileUtil.readBytes("C:\\Users\\ggk911\\Desktop\\页眉log.png");
        // String picBase64 = "iVBORw0KGgoAAAANSUhEUgAAASwAAAEsCAYAAAB5fY51AAAgdUlEQVR42u2dL1AjSxPAn4hAREREIBARiBMIBAKBiECcQCAQCEQEAoE4gUAgUoVAIBAIBCICgUCcQCAQCMQJBAKBQEQgEIgIBCKCb/er5lUub//07M7uzu7+uqqr3qsju/P3tz09PT3/fP3zzxeKomgZ9B8aAUVRgIWiKAqwUBQFWCiKogALRVEUYKEoCrBQFEVLCqx/EARBchKAhSAIwEIQBAFYCIIALICFIAjAQhAEAViZNsqOp22GB1LycbzmaQtgVRRYXmWuA+p44+kcwx8p4XgeTIzjF09/edoAWNXp4N8h9Rx52it7ZyO1BtYkuNYBVnU6ecXTcUSdfXhtMR2QkgJrWv2/mZ363e3Ev9/5492VjzXACm6UQ0VHX+HnQioALF8/5W/nfOsr5G9ePd3ztAmw3OzsH55+KDv8yNMZpoizfdn29HKiv8bSZ42K1/s0wXk8fwWxHOLPnf67g7yd+gArunEODDr6Aee8s/24FLLMv65yn3l16xvCyl/+deS3LRnTcb95l131BsByx6dl0un+xNgBE87145wsfcL6bLfGwNqPWWl8Goz/TC1XgKVrpMsEpvUxu4rO9eNJTJ+dVKnPDIDVi3lOL4Gl1gZYxXb+ecIcPXugwql+1Gyo3FdhqShLtdTAkmc1JSTCZOzvACw3lxRxa/wWqHCmH9vKzZTn6e3+Eta1ZwtY8rxuTMjPf/y6AKvYATBv2GH/Dn5QUUpo+fpY1vAV28Ca+HBr2+4JYBU/CG4SLg0XQYVT/Xhm0Hc3ZfRtyVlC60s38c9qnnsJsNwYCM8JgNUHE87146Ph7u98yerXzWpsKny6wywgD7CSNdpRAmD9AhHO9eO+YR+elax+SxkCK25puJdRnQBWDlbWR9FHGpDQfrw1hNZsierWUdbpNKWf7HVio+J0AmTW/X8AKx8r6xQ0ONuP24bA6pSobk1lnQYWATkMiMmaAVjl8YGcOVjuVU9/gqv/t0XDYNfruoT1+8zaOa48xnMKsIofDJsx7fPq2pa4DyoZxJ9A6982+aME1noJ6zbURKantOIelO23C7CKHxAtSfw3noDUiWu+DrGqPkN2v9Zq3oczMRPbb7flktbtTgGSYcJnJzn94bflKsAqfmC0ZQ3fcLBsqzFLg0+g9f8QgFFIGpX1iN8dCOxc1U9lyIbpc19TXCnvt+kSwEKmO3fJwD/zxTIxVVufGx7XygtYbw6W6VuND0kDrOpOoBUZhElMdqBlD1pjOUx9KP7DdgHjwOrxnILbOHtgiZ/nSoiKZq8fKcx1fFvVgqg2FmsPYP39osWEX3y0OP0EWqUHVqNKR8dyXxLGgOtNLLFDCehbEz9MBy1USf1cbmgNvypy7KgQH9YEtD4kJGBXUrGSoRNB7E/yp7yi3SsJLARxeEJslOn4jbJOgyyDRwEWghQzGWbF6vd1u0L10lz3dQ+w3K88GRSQ77EQdjvMR9lvQZKbm2MzigAs9yt/JuYyOdcB1r3iYoqO43VoBcXQGSTymwFY7lZ8JSDuqJ+V41/Oq62DBqvtuS99llbfDMM9rl2Cl4Dqdsoq/DH175p6dQGWuxU/irjBeT6DyXVNqmTrfbj6lewmIxs6dMGlEJHa5Xkyql4J5Q2A5Wall5WD8nfa5WLE0YgTwjis9ed6zK3Oj7L83xXIdad0YACqIxeWTpLWRXMhyrn8veYy4EOA5a7vSjtAX9OayhG3jAyAllVofaf4eZEPwqbm7J7BDclrjtRVkzDvryh25U03FwDLvQp3UywDniZ9AwkGWdjNubdlcHhWGHYqYBks+8uqrwDLvQr/Ttmp70kdrnLTyCjkuX/KemEnwPprmfZQcmg1AZY7lf2VMGvBwOZ5OvGlRDlzO2CkfMCasrYmwyReZAxtRmWiNbipuZuwnppU0JsAy53KPiaA1WJGZbmK8ZvNg5JyAmvCBbBt8vHJAVga3+0xwHKjogNDWI2yDij1nr8Qk7tqlNRnhhQLrITlyBpYK2X3Y9UCWJJvfWwIrL2cyrapAOcCWAFYFoDVUs6DNsAqroKNBIkD13MuY1ORs+gjzW0jCMCSd2h2MvsAq7gK9gxhdVdQOeOWh9/QWgIxACvFO/YUz78FWOWA1cCBMv9W+teWQQ3ASmjNa5aFSwAr/8o9GcZXtR0o85zyEglutwFYWc6LXwAr34qZBPDdunZERnn26xtcG2AHYBm8Z0PxjjeAlV+l1g1g9eFi6IBkv3x3daMAYJUaWNplYQdgZV+hbYNJfu/64WPD82lcywWwbNb5DmBlW5kZiRLXTu5OCSaTqaU1xrcFsBTvWlDOkRmAlU1FfkQcLA5K0tco2aS6M9zxJATCAWDJvY5DORbWdgVY8j7NUZ0bgJVNRX4bWFY/SjipFhNk1xxldRYSYKme25bMn9/PiIRWAcBaUlrsbYBlrwINgx3BqzInzBNn6euXecaJP1y0kR+wZEyGfUBXXAGWvFMTSPoIsPJt8O8MCO0KTK5eAmB9yZKS7Kb5AGuQJBNCQcDqKHcMuyne8X071b9aS2AZ+HWOKjbBFhMc5v7WLRCVDbAkvimsX1YVv88dWPLeVeV7Owme/TPogozaASsmCZ7TQaGW6n+VEFgf5NqyD6yYG3wGynLkFYflX8JyMLWEfdFeaGH4rn2b4RKlBJYyc+JnlL+gIhPtPEUq3ANQlR5YsqR6tXFxhU1g+R8lqdd9hNW3O/H3s8ojYeeGbRsEwrPaAMufaMpO3a7BRNMOsjBLC0d8CmDJLmCUZXKVkX+yG/BbP9fVmviI4jZm/DLvTPe/8madsTY/W0T+rX7lgWWwQ3ZbJ+eyQCupP+sSXJkDS5ZQcf7TjQTl6Bmc0Bgq//ZZNqZmLbsaNhTP2g/5ba8OwLpU7oTN1HDCHSYE1oidw0TAuogLtkzSril2gIOsoIskvi6JgB/b2HWP2C39WVlgKSPY3+vuSDa8HHZS9wGW+iLVfhauCPE37RumQ/qaCkjdshW2IwGlWqt9LeQZcxG/+VFJYMka+FlhJSwx6f4P9iRLwwvaTg2sLxuWlSwpf8pHZpjifdfynEYGbXJuYKXPB/y+m0UWCGeBpQwIJaXK3232M8mykHazAqytgI/tlkDp1cLzR1LOVo7tcmxQvr9uL4/xhc1UClgxMS212QVM2HbPppOBNksNrBvJFLIqk/w5xUZI0MW6/SJ2dBMcBbv9vjk6avc0ZZncAZZyCQiootuwbXhI+oM2S3W9/PbUsvwjpbP8RiLmGw61j2m831tWzHAGWLKu/wOsrLSlSdK/p5q31UIKa+g64Hm9hKAauLpxJNbjkyWL8SsNjJ0AlveQU7JoWh9gbwTYxrbTjxSw6kc8d16xs31ftp1t8cnZWOqW1+kuEbdRXx5glaxdD5WO3Jmat9OvBBPu7dtXk3Bcv5Q1c4hX7hMLwFoqHbBkR+sjwtHYATuZfgye6w4rAyv/ryW0dkkTcHTqsyr3SaY8x3pcKmBJepRRRLT6LFPI2sC6CPnCk7Hhv22lCb79afjM3TTHdUpgbY1t+P6cBNZEfmsjnwCC5DgJmyG7huOkkI9bPlagzRYNnfLjpG2SG7DEPB6GZA0gABRxaQK25LiLcU6rGrfZTMTZwVRpd3IHVshVVZekN0Ecn4Rz4mA/pDWMrdS4ZAVD1y2s3oQ5uEu3IiVa7szREonabjVmqdhO8MzqXVWPIIiTAJuVQ9FLSYNHARaCIGWCHsBCEARgIQiCACwEQQAWwEIQBGAhCIIALARBABbAQhAEYCEIggAsBEEAFsBCEARgIQiCACwEQQAWwEIQBGAhCIIALARBABbAQhAEYCEIggAsBEEAFsBCEARgIQiCACwEQQAWgiAIwEIQBAFYCIIArMo1TMPTC09/edpkqCAIwHK1UWY8vZqo55un+562GDKl7dMOrZDZh30dYBXfEXOejkLqPfR0y+8shmxp+vNY+m7s6aH/UaJVUrfpkrTn97x49HQWYBXXIRtTHTKt956uMHRL0Zf9gI9Oj5ZJ3J5NT58C5oS/GlkCWMWavA9x7SDW2DZD2dl+3I7pvzOW/Oq2XI/5kH/riW1LFmDpGmne0w9FB/n629M2w9q5Puwp+u7d0z35WzRc35Vzwddbm/MBYJlZWn8MOsoH3A9Q4Uz/bRn0HWpX/dXHT4CV/6BfUJrC//q4QIUzfbeq7LM78XfZ1uHUe17E+d8voZ4YzIFXmz4tgGXeYLOefhp02Am4cKLfusr+6mfw7kUFJLslasuOsi17ON3d6LBzA2C9gIvaA+vQwLr7AbAAVhaddmMArVbBZW1IjIxvns/XtL86RQBLNmy0boRPOV2xALBKCiz/ixOwS7HkQKdtKzvt2oGynk3thK0ArNyA1TeA1Y+M6t4AWPkBK8iSuS3B0vDT1s5IivLNxWxBbwOs7IDl97/ynU9ZnVf1P5gT43ETYGU/0IIm3KMjk2BWllrT5fMhu+xA+f4U4bMBWP++81b5zpWM6nwU8K59gJXdIIuKnemiobpjGH7xHeDXAlh2gKXcmHnJ4kyj1DUuuHOQdKkIsMILd0HQXa6a+eHVOgDLj+xWhr6cZxSJ/qSsayJoAazggi3HDSzUSmBfUCaK46plosgLWJKa6K2EH6o2wLK3sxWklbQELLXdoeGAfRdIdVgSpgaW5sziKMOP1WMCYJ2aOP0B1n8LpdkK7oKmwLbbUw6myzpBPw9gGYS6+Ppse2dwYkfwS7kcbWbcltUHlu/4jUicB7DSt91Hnhki6wIsyQ/1amjZ7Fus34bhErCZQ1tWG1j+9q7B7tYCiPqP7+Q1Jtlgu8btkxmwlLAK+5AMLNTt2ABW2zm2ZXWBJcdHngwavlOzCdeI+ff9iLa6qPtFGhkD60ThI/qVxWpBNqe0H/njnNuymsCSiGCTuKG7Gk20lYmYmmGQlRQBq1tyl2cLLEnYGPW8VaUllASUxwabKnMFtGX1gKUMcpvWXzWZZIsBS4nHyQDPiOXIMyl/swWW4ujNwdTfz0Zkrv0w2QSRAGHtR369oLasFrBCjg7k3giOgioO4jfit3oPGPhLICpbYPkwiolla0f4Gt/TOOINMoXcZRFPV2dgnRrC6ghY/cc3tTx1ScYueMoWWNJHHxGH3pdSwC7y9zG/nb69Zq7gtqyu010KMyd3na3JOcLViC9VL8F5uarqBXck5gOsmDi3I4PyrCsuM2lM/H3b4EO240hbVhtYCQoPtGqWdaFIYElutg9bHw1FoOeR/J3JBShHDrUlwFKA61N2x47EKdoqyYTaSwHfD/FrcKNxRsCKOH3xmGaMxYQ7fBmMidw2WQCWnYqsCaBmSjiZzhJAaixg3q57jFXWwIqIdRqmPd4k1tO9hWu0fjjYlgCrQhOoZThQR2I1cug7R2B5/38VcnVVx3L5+glhteVwWwKsikyeuamdvbhc38fEVOUPrJAUR29ZhY0YZCj91j3H2xJglXzSLCl3eoZF54MHWH/9tinW7XxGZWsb3ir+HYc3C7AAVladvBmxwzTplzrCJ+UWsDIu10KCkx6TboKtvENaAFa1J0pccOy7wIw4qhoBSzZLbIbkfIgfbAZgAazJBlvTdITs/gwUObXxTdUIWHI059RgJ/jaEFzPkhurAbAA1vSV56Pp/EKy+xflj7jK6sgE4i6wxIp+T+qfknGXZOl4LZsHDYBVT2CthCTHW5COfIww2Xss/eoFLHHYnxrsCPcinjWbwDk/GeDas2HRA6xqDvxvk/4YJ3q9gCVW9rnSR/Up+cxMj/WsGia8DLsXsW966S/AqubA/8zqRl9L9WjRb5ncmrNlsHT7kybVt4Dx1JLz/lWetREXLgGwyjf4TQfDsStHhgLCK7YBVqqMoybW1Fig0MqojrsJLsIwvbPy1mAjAGA5MvjHCTp6wYFyH4SUfRtgGSfwm5ED6yMlqC6+A0/FlzSUXeI9cYrPWKpnU5Z5n1//FJ5BBGA5MvhflHExu0U72Q3PLG4DrMj0MjvKvh+L1dUJec6ysk9eJ66z7ySsd1cyj6aFz7PUf5YlYfkGv2YA3DlQzpbBmcVKQyvF4ecZCfJ8tn2DtjjbPwyd5GcSC2jqpF+SkJok/q6DSQuw8sCSlLLdCumjchu5qPJtyiHdNF/U7ZoC61yOR70prKgrWxf6GsZpBY215QQfs31ZmprmBqsFsN4dWFOjNYWWYThKnJWzn8WltJLF4yZhucZJUs3IiYyNAOvrJcyvVpsloQG4XmU3YiBZNPuO6VDpaO9XRNs1BNabjL/VInZ4JRD5McnyzdL7Zyy0Zfl9WAHQGgmcDmRwtEow+Evhw0ISTTLrR1lS7vhdmqadcagtcboDLKSoXcKCy7+mdNBfASyABbAAlgt1WFG4VLg1B2ABLIDlVF0WQyLa+461JcACWEjdgSX1mZGAUj8M4yTPlNoAC2AhFQGWhAjcy83NRxIC0ZP4uE4V7o0EWAALqQ6wesqwnT+yI3kkl6muys3STYAFsAAWwMp9SSi+pmPlsR6NvgvoTuVc30IR4RcAC2AhFfdh+Zka5DD8neVMCp8CMd+Ptc4lFAALYAGsrMulPT1hqmdZBGIDLICF1BRYU2VcEciMLEJrmDQ1DcACWAjAMi33qqV8Vn2ABbAAFsDKq/xLCe4onHbYNwAWwAJYAKuI+mwqs59OahdgASyABbCKqpPJnYcAC2ABLIDlTP3mY9JhDy1edAGwABYCsFLXsSFHgUYBkfVLBbQlwAJYCMAqTVseACyAhQCs2rZl5YElxxVuJCBvX07T+2e85lKY3e8Aq3STbIlLRXJXgJWggr2Yc1iPEt9yNpUKxD85PxsAqyNlZwEst8ZBF4DkricAK7kJuy05ikY5ddYxmCglsFgS2lsSDgBW+gr7mRp/yk094wyBtcjQBlg1B9ZvgJVNBzTlmjFb1leXYV0q1wDAygZYTwAr246YTXHzLktBgAWw/tZ7gJVfp6wr74H7F1RVyNdd4f7cV6YuXqe1EgPrVdI9NzN8N8CKaJxVhZ/LD3FYYxg735f9qNuS88rWWVFgjaR9mzm8G2DFNNDMVDZIPxTiyuZRBySXfjyXj8+VfIgatEritpyTFUWngHcDLEUj7Xn6Jo75OYZsKfvQz+bZpiVK348AC0EQgIUgCAKwEAQBWAALQRCAhSAIArAQBAFYAAtBEICFIAgCsBAEAVgAC0EQgIUgCAKwEAQBWAALQRCAhSAIArAQBAFYAAtBEICFIAgCsBAEAVgAC0EQgIUgCAKwEAQBWAiCIAALQRAEYCEIArAQBDGcfFzKC7AQpBQTr+fp2NNHT7c9naFVSgYs72G7nu572qRrrLXpjacvMkEatIgz/TIImVOfnp542qGVHAaW96BZT0fy3KGnGxafvcnE+L/64NrztMXQL7xfGjLOw+bWWMBFXzkKrK50UlDHnaaxurzfvk8866gIC06A/CDlOMzxvYcR/fcpbYsvpbgJeB03x0QvsJDdXBKGvcv/Gq0lfO7Q1rMSvr/l6dNUGQ5zendfMRk+ZUIsgZDcJ+CKEli+9nMq06KnCwBLV6CmLF2i3vtqApwY09ufrAdZOD19P0TMuzMHl/gEtRPCt0B7YCT3Sbhl0EcnGX9YHyfe9expG2DFF2o5ZHk4rfsaM1kaPu5ZQ7FGbOqw6C+nONu1k2EDfBQyCdvK8e7rXYawegh434NLO5jOhjX4jaQEzfeXYDbiWXcGk7ZI7RcErCHYKHwiHivHyEoG8+xG8d4zgBVfuDWDyR4KLSWwbsTxb1MHBuX3rbFlgFVbYPWKgIb3zEuDMboDsOILuGRgLn8FOQuVwBoU5PDeyXL3x3v2T4AV2jZdAYULehTTR8eW33eScBXwWeTmTCki3b2X/DZo0F8Bv79xGFidHCYlwApum45igwf9r46K2kUszdGcEIegyimpXJo5ByyxLv1dm1WAlenYmlOCy9+dvpXxdDixuTL9d0cZbOBoNOjD/D5VVpu6CbDCC7qnBNagQsCam/i7P/7yDmDlZm29iX9nT2KlWgZLbv856wXUIWwl8piFfxQLK76wpzHlOQ/53aCsS0LxGQT9zgfYRpz/C2DlMi4XIvysY/E/zWRchjWDzaklgJVPYZsBEeO+fsiXsFFBYMXFcr1EmeYAK7exGTfGHrL0V8pJBZMA4ZMyZoioRXqZNMASIGwk2cmzBKxHQ4fo4+RABFi5jrPDIuLt/OWnwfh4cPXYDcCyA6zJju4WAKw7wy/nJhZWYeNsVqz9XGOaDKyro7IfoAZY8cAKW5LdxjkycwTWZ9iyEGDlPtZMDjPfWXjfruI9mxVq3/yAJY17mXeOH98ZnwGwvvUqDDqWgHWr+XLiw3JqUj0ZQKuV8l3PpnGJGdTXP9P7o1LACjkC4E/G+Rwq2c8QWNNLsn3LwBqk8YkALKetrM80ju+YYzUnGddxJmBsPmWd3SFPYK1EJOjbrQiw/CC9RYBVe2A1I8JR/rLOU7yjExFKcZfxca+oA9NPWSbIzN2HJfFDYc97SxIcOfV8P03GmST27+QErLeg2BaAVWtoxeUhO0k5xkchR2Y6GdapZbAJdFEVYC0qvj6nSb8QvoMx4DjFOGNgDTL0YQGscgKrFbFjOEhjAUXAcDPD+swlCLE5LD2w5KUNZa6rFxNnnpiqbyGxJ0mhcwewSgOJRfGL3jmkTxPWz73F503rfYZ1eE95WHq91MCSF2uD3V4n/UIxz1wNMZMPAVY9LKyJ+/7IquCOftg6DlRoHFYIYKIidOPOzQWZrP2UPiyAVT1wvYkldiK+zlU5D9hBM9V2qYElBXg2gNZOxHMWQn7TBVj19GFNQOtB4LTFBaWl79PCgdUM2fEIjHOKeE7Q/W4PFnYJAVb9JsWvou6vLLDOq2I8zAOs+EKcGlhZswG/DzvDdQywAFZCy+x76bhb9QtM/Y2tCaPhw+WjPK4Ay8TKmgn4/UVIQGoLYAGsBOOxm+f9lQXXNSqX/D33EoYX5Ewxse4DftcI2XZ9MARHGmCdA6zKA2vydqOtitRzS7Gj+qzdpa8VsCbW0aMAS+k2bFs0ImRhLUdg9QFWpYDVIRQheJwVvTx28ar6tiTM68n5w2bM3z/FLR0BFsACWNZ0HWAlL/yeZomWEliasIsewKoctAq7Nj7HOmov+v3jii+r7MAKO0i9YRFYmrOEmwALYFUUWG8ubTSUFlgRB0BfEoIjDbC6AAtgVRRYwzL1i8vACrOuTgsA1gLAqhywRgALYNkq9E5EmdqWgaX50jYAVuWANQRYAMtWoa/DMjukAEdSYI0iygmwABbAqjOw5HR9WHm2Qn6TKL2M5NeK3UEBWAALYAGssALfmE7+pLfmKONxrgFWbYH1LPGCZdV7gJVtYaPyZF+mnPRBwFpW/O4gJbC6AKu0wKqDAqwUhY1KdbyXAbA0WVG3ARYWFhYWwJou6EnM9VqNDIB1oPjdPMDCh1VhH9YXwDIvZCMmEf6NhY4JApYmT1crJbA2ARbAAljVAtZDDlZKELDizhHex7xXA6wewAJYLgPLpQSGzgNLMeFir/tOAiyx6uJyBV1YANY2wAJYjgOrA7B0hdP4kI4Vz7lJAKxtxW9WLQCrD7CcnMxjgAWwTAq2phw0PxXPuksArBPFb9oWgHVmYVCdA6x8JwbAqjCwIszrD7nd2QfKlTTgpcFFmPfK9xsBS5aDnzF/f6V4bxSw7gTKDduwBVi5Aasu2q0bsC4UAEiihxkBa1Hx9/sJgXVtcguusuxXAMvqpGgAKb3roxZLQrEu7lI04li7e6FwoL5POr8jUtdMvrupeO+OnGNspWinuzRLE4CVqM07eS8Jlf1k1dpRLgn9FdAKPqy/O+o2AbBuLez4PMq9c82Jv51VLEnvcuwgTYrmJ4BVCLBuKwqssbhoVl27k9Gla74aingrdcBlwI7PUM4ixjnKNfBc8N8vltl6xh2k2V4fAyyrbd4NsMCvpM9ncnpnXsByFk5OA0sKs2TgcDfxA61rBprc2DNWmMitKZCcZnWtucL3dxdzjhJgmbf5pmwIXch/NwuAZB7AWrFxuYT4fE/zAJ6L13xdpbEoUr5bY+H513rPhVg+n1HZGxKUpz11C+8v/90ZTASA5Z5VV4Ydu18BH/jDugFL4wh8zeC9SyZOVlkWhp1v9ANVZy0BazdNHAzAAlgZlfcg7e59VYD1kHfAnjhZNUvBRsBv44JD90owEQAWwNKUsak8NRKZAbhqwBoVAKzjNBH1imXsdZrQBoAFsIoGluyePxju5PsukrXKAktis/KOf7lTNPoPxXM02UlHtjsQYP1V157Bpg2an37aCj51DVhHeQLLN1ltRLQbWmq+7gAsoFUz/TDZ2XceWLLzpq38Uw6W1VsSx7nEs2jrcQKwcgOXv6v7W3a2ll26fr2MTvcC28kZYO3llRhfkbZmnGZgGDom9wBWZnXfFEAt4MMCWLaBNTKY5KMUgyIuEPOXpfpcGtRnALBwugOskgBrKkBSpQneMR+TF96qtSNLXK0vZRx1mQXAqsVE3ABY5QFWL4ETr6N8th9j9ZQmTXGKeq0Y1Oc14Pf9qfNsj2K59WXDYFlxNvIYYJXG5wawSgKsrQTAWlRaOS8x263bGTfwY1KrUZmx9Lsej+I7O5PfbYv/5l3x+z8gozTA+gmw3PBhmebG2ol41ppso35FpJXp5NTAs8r6XCuWzX62idcMtpxPQEbhE3Fb2Vc9gOUGsFqKpVusv0kRhzPIO+pcecPuuvJZTbGgPiwCax1kFD4R+wCrRMCKsSw6os0I2N3FXCm+WHBD74f5j1Iebt5NmX76ElyUClh7AKsEV9XHLP+GEbtvJ64ECUrK5OcJJ/qFpXxEqwktro+sdieRzIDVB1glA5aEKNxETMJ91yKZc2qXTWUoxVPRFifyn74bFB2zB7CyKXAjwvF8VXeLQXYJo44b7ZYhFS7ACtXfAKucFtZYwNWrozWl8Gv5cDq3ceAUyaXPrkNWC9dpkzgCLAd8WH48Sh65tkvaqQ0gXro+u5uA1KXEJrZomYoAC0EQgAWwEAQBWAiCIIUDC0VR1BUFWCiKAiwURVGAhaIowEJRFAVYKIqiAAtFUYCFoijqqP4P/9rLBiutH9EAAAAASUVORK5CYII=";
        // PdfUtil.FillImageParam imageParam2 = new PdfUtil.FillImageParam("Text3", null, "90", "", "true");
        // PdfUtil.FillImageParam imageParam3 = new PdfUtil.FillImageParam("Text4", picBase64, "45", "", "");
        // PdfUtil.FillImageParam imageParam4 = new PdfUtil.FillImageParam("Text5", null, "180", "", "true");
        // PdfUtil.FillImageParam imageParam5 = new PdfUtil.FillImageParam("Text6", null, "45", "", "");
        // Map<String, Object> fillData = new HashMap<>(7);
        // fillData.put("Text1", "\uD86D\uDCE9\uD873\uDC56\uD86D\uDCE9\uD873\uDC56\uD86D\uDCE9\uD873\uDC56");
        // StringBuilder s = new StringBuilder();
        // for (int i = 0; i < 26; i++) {
        //     s.append("唐好凯唐好凯");
        // }
        // fillData.put("Text2", s.toString());
        // fillData.put("Check Box3", 1);
        // Map<String, String> textData = fillData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> String.valueOf(entry.getValue())));
        // fillData.put("Text3", imageParam2);
        // fillData.put("Text4", imageParam3);
        // fillData.put("Text5", imageParam4);
        // fillData.put("Text6", imageParam5);
        //
        // // 只填充文本
        // byte[] bytes = PdfUtil.pdfFill(pdfBytes, JSONUtil.parse(textData).toStringPretty());
        // // 填充文本+图片
        // // byte[] bytes = PdfUtil.pdfFill(pdfBytes, fillData, pic);
        // FileUtil.writeBytes(bytes, "C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\picAndTxtFill.pdf");

    }

    @Test
    @SneakyThrows
    public void createPdfTest() {
        BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

        // 加粗， 斜体，删除线
        Font styleFont = new Font(baseFont, 12f, Font.BOLD | Font.ITALIC | Font.STRIKETHRU);
        // 正常字体
        Font normalFont = new Font(baseFont, 12f);

        System.out.println("//*************************************************创建一个PDF**********************************************************//");
        // 1.创建document实例
        // 自定义页大小
        // Rectangle pagesize = new Rectangle(216f, 720f);
        // Document document = new Document(pagesize, 36f, 72f, 108f, 180f);
        // rotate横向显示
        Document document = new Document(PageSize.A4.rotate());
        // Document document = new Document(PageSize.A4);
        FileOutputStream out = new FileOutputStream("C:\\Users\\ggk911\\IdeaProjects\\test\\src\\main\\java\\pdfTest\\create.pdf");
        // 2.创建PdfWriter 实例，并指定输出路径。
        PdfWriter writer = PdfWriter.getInstance(document, out);
        // 设置PDF版本
        writer.setPdfVersion(PdfWriter.VERSION_1_6);
        // open前 设置行距
        writer.setInitialLeading(100f);
        // 自定义事件:页眉和水印
        writer.setPageEvent(new PdfCustomEvent(baseFont));
        // 3. 打开 document实例，开始document中添加内容
        document.open();
        // 4.添加内容
        document.add(new Paragraph("test......."));
        document.add(Chunk.NEWLINE);

        document.add(new Chunk("Chunk"));
        document.add(Chunk.NEWLINE);

        /* Chunk */
        Chunk chunk = new Chunk("UNDERLINE");
        Chunk chunk2 = new Chunk("E");

        // 下划线
        chunk.setUnderline(0.2f, -3f);
        document.add(chunk);
        document.add(Chunk.NEWLINE);

        // 上标
        Chunk id = new Chunk("2");
        // 正数>0
        id.setTextRise(6);
        document.add(chunk2);
        document.add(id);
        document.add(Chunk.NEWLINE);

        // 下标
        // 负数<0
        id.setTextRise(-6);
        document.add(chunk2);
        document.add(id);
        document.add(Chunk.NEWLINE);

        document.add(new Chunk("Phrase"));
        document.add(Chunk.NEWLINE);

        /* Phrase */
        Phrase p = new Phrase();
        p.add(new Chunk("正常", normalFont));
        p.add(Chunk.NEWLINE);
        p.add(Chunk.NEWLINE);
        p.add(new Chunk("样式", styleFont));
        p.add(Chunk.NEWLINE);
        document.add(p);

        document.add(new Chunk("Paragraph"));
        document.add(Chunk.NEWLINE);

        /* Paragraph */
        Paragraph paragraph = new Paragraph();
        // 直接添加Phrase不会分段换行，只会超过行限制后换行，默认行距
        paragraph.add(new Phrase("国国国国国国国国国国国国国国国国", normalFont));
        paragraph.add(new Phrase("国国国国国国国国国国国国国国国国", normalFont));
        paragraph.add(new Phrase("国国国国国国国国国国国国国国国国", normalFont));
        paragraph.setSpacingBefore(10f);
        document.add(paragraph);

        document.add(Chunk.NEWLINE);

        Paragraph paragraph2 = new Paragraph();
        // 会分段换行，会超过行限制后换行，但是是默认行距
        paragraph2.add(new Paragraph("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国", normalFont));
        paragraph2.add(new Paragraph("国国国国国国国国国国国国国国国国", normalFont));
        paragraph2.add(new Paragraph("国国国国国国国国国国国国国国国国", normalFont));
        paragraph2.setSpacingBefore(10f);
        document.add(paragraph2);

        document.add(Chunk.NEWLINE);

        Paragraph paragraph3 = new Paragraph();
        // 添加Phrase手动添加换行符会分段换行，会超过行限制后换行，不是默认行距，段落自定义行距生效
        paragraph3.add(new Phrase("国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国国\n", normalFont));
        paragraph3.add(new Phrase("国国国国国国国国国国国国国国国国\n", normalFont));
        paragraph3.add(new Phrase("国国国国国国国国国国国国国国国国\n", normalFont));
        paragraph3.setLeading(20f);
        paragraph3.setSpacingBefore(10f);
        document.add(paragraph3);

        document.add(Chunk.NEWLINE);

        Paragraph paragraph4 = new Paragraph();
        Paragraph paragraph4_1 = new Paragraph("GGK911");
        paragraph4_1.setAlignment(Paragraph.ALIGN_RIGHT);
        Paragraph paragraph4_2 = new Paragraph("GGK922");
        paragraph4_2.setAlignment(Paragraph.ALIGN_CENTER);
        Paragraph paragraph4_3 = new Paragraph("GGK933");
        paragraph4_3.setAlignment(Paragraph.ALIGN_LEFT);
        // 两边对齐好像没效果
        Paragraph paragraph4_4 = new Paragraph("GGK944");
        paragraph4_4.setAlignment(Paragraph.ALIGN_JUSTIFIED);
        paragraph4.add(paragraph4_1);
        paragraph4.add(paragraph4_2);
        paragraph4.add(paragraph4_3);
        paragraph4.add(paragraph4_4);
        document.add(paragraph4);

        /* 表格 */

        // 流式添加元素，必须设置列数
        PdfPTable table = new PdfPTable(2);
        // 添加单元格
        table.addCell("cell0");
        PdfPCell cell = new PdfPCell(new Phrase("cell1"));
        // 跨两行
        cell.setRowspan(2);
        // 左右居中
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        // 上下居中
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        table.addCell(cell);
        table.addCell("cell2");
        PdfPCell cell2 = new PdfPCell(new Phrase("cell3"));
        cell2.setColspan(2);
        cell2.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        table.addCell(cell2);
        // 单独一列，没有补满一行，这行是直接去掉的，要搭配completeRow直接布满空列，才显示全
        table.addCell("cell4");
        table.completeRow();
        // 默认宽度80%，补慢
        table.setWidthPercentage(100);

        // 嵌套表格
        PdfPTable table2 = new PdfPTable(3);
        // 设置单元格宽度比例
        table2.setWidths(new int[]{2, 1, 1});
        // 去掉这个单元格的边框
        PdfPCell in_cell1 = new PdfPCell(new Phrase("cell0"));
        in_cell1.setBorderWidthTop(0);
        in_cell1.setBorderWidthRight(0);
        in_cell1.setBorderWidthBottom(0);
        in_cell1.setBorderWidth(0);
        table2.addCell(in_cell1);
        // 去掉这个单元格的边框
        PdfPCell in_cell2 = new PdfPCell(new Phrase("cell1"));
        in_cell2.setBorderWidthTop(0);
        in_cell2.setBorderWidthRight(0);
        in_cell2.setBorderWidthBottom(0);
        in_cell2.setBorderWidth(0);
        table2.addCell(in_cell2);
        // 无法动态宽度，超过宽度，换行
        table2.addCell("cell2cell2cell2cell2cell2cell2cell2cell2cell2");
        table2.addCell("cell3");
        table2.addCell("cell4");
        table2.addCell("cell5");
        table2.addCell("cell6");
        table2.addCell("cell7");
        table2.addCell("cell8");
        // 嵌套子表格不适合设置上下间距
        table2.setSpacingBefore(50);
        table2.setSpacingAfter(20);

        PdfPCell cell3 = new PdfPCell(table2);
        cell3.setColspan(2);
        table.addCell(cell3);

        document.add(table);


        PdfContentByte pdfContentByte = writer.getDirectContent();
        // 二维码
        BarcodeQRCode barcodeQRCode = new BarcodeQRCode("www.baidu.com", 50, 50, null);
        Image img = barcodeQRCode.getImage();
        document.add(img);
        // 条形码
        Barcode39 barcode39 = new Barcode39();
        barcode39.setCode("123456789");
        Image code39Image = barcode39.createImageWithBarcode(pdfContentByte, null, null);
        document.add(code39Image);

        Barcode128 barcode128 = new Barcode128();
        barcode128.setCode("baimoz.me");
        barcode128.setCodeType(Barcode.CODE128);
        Image code128Image = barcode128.createImageWithBarcode(pdfContentByte, null, null);
        document.add(code128Image);

        BarcodeEAN barcodeEAN = new BarcodeEAN();
        barcodeEAN.setCode("3210123456789");
        barcodeEAN.setCodeType(Barcode.EAN13);
        Image codeEANImage = barcodeEAN.createImageWithBarcode(pdfContentByte, null, null);
        document.add(codeEANImage);
        document.newPage();


        // 5. 关闭
        document.close();
    }
}
