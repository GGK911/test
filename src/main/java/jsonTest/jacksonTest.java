package jsonTest;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import extense.Father;
import extense.FatherSerializer;
import extense.Son;

import java.text.SimpleDateFormat;

/**
 * json测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-23 12:00
 **/
public class jacksonTest {
    public static void main(String[] args) {
        Father father = new Father("A", "18", DateUtil.yesterday(), DateUtil.date());

        // 普通序列化
        ObjectMapper mapper0 = new ObjectMapper();
        try {
            System.out.println(mapper0.writeValueAsString(father));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 自定义序列化器
        ObjectMapper mapper1 = new ObjectMapper();
        FatherSerializer fatherSerializer = new FatherSerializer(Father.class);
        SimpleModule simpleModule = new SimpleModule("fatherSerializer", new Version(2, 1, 3, null, null, null));
        simpleModule.addSerializer(Father.class, fatherSerializer);
        mapper1.registerModule(simpleModule);
        try {
            System.out.println(mapper1.writeValueAsString(father));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 日期格式
        ObjectMapper mapper2 = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        mapper2.setDateFormat(dateFormat);
        try {
            System.out.println(mapper2.writeValueAsString(father));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Son son = new Son("son1", "5");
        father.addSon(son);
        try {
            System.out.println(mapper0.writeValueAsString(father));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
