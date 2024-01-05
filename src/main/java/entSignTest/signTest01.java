package entSignTest;

import cn.com.mcsca.extend.SecuEngine;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

/**
 * @author TangHaoKai
 * @version V1.0 2024/1/2 17:11
 **/
public class signTest01 {

    @Test
    public void test01() {
        String pri = "MIGHAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBG0wawIBAQQg2TSyBsSej2+rzLbzosJISpHpvxnHkytt/ZFya/v3bk6hRANCAASNnEXTr1xvyeiCFyjvJMCitVR0UJ63UzfU4ftoGA0ejs3Pn/CLatCIzLfwjzimiqGf7jG8UxfOEdDY8QElLkqe";
        String sign = "MWkwGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMjQwMTAyMDkxNzI1WjAvBgkqhkiG9w0BCQQxIgQgTKUERQO0vWiKOG5OARtuv7WLzm7iP1yGDmrNWhPGS6c=";
        try {
            String signDataBySM2 = new SecuEngine().SignDataBySM2(pri, Base64.decode(sign));
            System.out.println(signDataBySM2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test02() {
        String data = "3169301806092a864886f70d010903310b06092a864886f70d010701301c06092a864886f70d010905310f170d3234303130323039353833345a302f06092a864886f70d01090431220420254e1c615194486468b23497ea4899299d5e249165cc8d11556231be1726cce2";
        System.out.println(Base64.toBase64String(Hex.decode(data)));
    }
}
