package listTest;

import extense.Father;
import extense.Son;

import java.util.List;

/**
 * @author TangHaoKai
 * @version V1.0 2023-10-20 11:59
 **/
public class test {
    public static void main(String[] args) {
        Father father = new Father();
        Son son = new Son("A", "18");
        // 这里在Father中已初始化List所以不会报空指针
        List<Son> sons = father.getSons();
        father.setName("123");
        sons.add(son);

    }
}
