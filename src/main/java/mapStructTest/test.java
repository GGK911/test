package mapStructTest;

import cn.hutool.json.JSONUtil;
import extense.Person;

import java.util.Date;

/**
 * mapStruct测试
 *
 * @author TangHaoKai
 * @version V1.0 2023-11-01 15:14
 **/
public class test {
    public static void main(String[] args) {
        Person person = new Person("ggk911", "18", new Date(), null);
        PersonDTO dto = PersonMapper.INSTANCT.conver(person);
        System.out.println(JSONUtil.parse(dto).toStringPretty());
    }
}
