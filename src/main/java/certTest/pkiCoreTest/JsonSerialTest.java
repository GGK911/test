package certTest.pkiCoreTest;

import cn.com.mcsca.pki.core.util.SignatureUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/8/14 9:39
 */
public class JsonSerialTest {
    @Test
    @SneakyThrows
    public void serialTest() {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> head = new HashMap<String, Object>();
        head.put("serialNo9", "2222");
        head.put("serialNo2", "2222");
        head.put("serialNo3", "2222");
        head.put("serialNo5", "2222");
        head.put("serialNo4", "2222");
        head.put("serialNo1", "2222");
        head.put("serialNo8", "2222");
        head.put("serialNo7", "2222");
        head.put("serialNo6", "2222");
        head.put("methodCode", "1111");
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("appId","af");
        body.put("appSecret","aa");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("applyName", "xqj");
        param.put("applyCardNo", "222");
        param.put("applyCardType", "01");
        body.put("param", param);
        map.put("reqHead", head);
        map.put("reqBody", body);
        String sortJsonStr = SignatureUtil.getSortJsonStr(map);
        System.out.println(String.format("%-16s", "sortJsonStr>> ") + sortJsonStr);

        String jsonString = JSONObject.toJSONString(map, SerializerFeature.SortField);
        System.out.println(String.format("%-16s", "jsonString>> ") + jsonString);
    }
}
