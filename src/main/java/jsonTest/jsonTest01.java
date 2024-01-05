package jsonTest;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import extense.Person;
import org.junit.jupiter.api.Test;
import pdfTest.PdfUtil;

/**
 * @author TangHaoKai
 * @version V1.0 2023-12-20 17:03
 **/
public class jsonTest01 {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("key", "1");
        jsonObject.set("base64", true);
        String toStringPretty = jsonObject.toStringPretty();
        System.out.println(toStringPretty);

        PdfUtil.FillImageParam imageParam = JSONUtil.toBean(toStringPretty, PdfUtil.FillImageParam.class);
        System.out.println(imageParam.getBase64());
    }

    @Test
    public void jsonToBean() {
        String jsonStr = "{\"name\":\"唐好凯\",\"age\":\"100\"}";
        Person person = JSONUtil.toBean(jsonStr, Person.class);
        System.out.println(person.getName());
        System.out.println(person.getAge());
    }

    @Test
    public void jsonToBean2() {
        String jsonStr = "{\"array\":[{\"name\":\"唐好凯\",\"age\":\"100\"},{\"name\":\"唐好凯\",\"age\":\"100\"}]}";
        Object array = JSONUtil.parseObj(jsonStr).get("array");
        JSONArray jsonArray = JSONUtil.parseArray(array);
        for (Object o : jsonArray) {
            Person person = JSONUtil.toBean(o.toString(), Person.class);
            System.out.println(person.getName());
            System.out.println(person.getAge());
        }
    }
}
