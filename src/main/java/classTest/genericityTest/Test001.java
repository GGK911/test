package classTest.genericityTest;

import classTest.genericityTest.interfaceTest.AA;
import classTest.genericityTest.interfaceTest.BB;
import classTest.genericityTest.interfaceTest.CC;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * @author TangHaoKai
 * @version V1.0 2024/10/18 14:43
 */
public class Test001 {

    @Test
    @SneakyThrows
    public void interfaceTest() {
        // 当去实现 IA 接口时，因为 IA 在继承 IUsu 接口时，指定了类型参数 U 为 String，R 为 Double
        // 所以在实现 IUsb 接口的方法时，使用 String 替换 U,用 Double 替换 R
        AA aa = new AA();
        Double v = aa.get("1.2");
        System.out.println(v);
        aa.hello(v);
        Double test = aa.test("test");
        System.out.println(test == null);

        BB bb = new BB();
        Date date = bb.get("20241019152301000");
        System.out.println(date);
        bb.hello(date);
        Date test1 = bb.test("test");
        System.out.println(test1 == null);

        CC cc = new CC();
        Object o = cc.get(123);
        System.out.println(o);
        cc.hello(o);
        Object test2 = cc.test(123);
        System.out.println(test2 == null);
    }

    @Test
    @SneakyThrows
    public void methodTest() {
        classTest.genericityTest.methonTest.AA<Double> aa = new classTest.genericityTest.methonTest.AA<>();
        Map<String, Double> map = aa.getMap("aa", 1.3D);
        System.out.println(JSONUtil.toJsonStr(map));
        Double u = aa.getU(1L, 1.3D);
        System.out.println(u);

        // 一、不显式地指定类型参数
        // 传入的两个实参一个是 Integer，另一个是 Float，
        // 所以<T>取共同父类的最小级，<T> == <Number>
        Number add = classTest.genericityTest.methonTest.AA.add(1, 1.2);
        System.out.println(add);

        Serializable add1 = classTest.genericityTest.methonTest.AA.add(1, "1");
        System.out.println(add1);

        // 二、显式地指定类型参数

        Integer add2 = classTest.genericityTest.methonTest.AA.<Integer>add(1, 1);
        System.out.println(add2);

        // Integer 和 Float 都是 Number 的子类，因此可以传入两者的对象
        Number add3 = classTest.genericityTest.methonTest.AA.<Number>add(123, 123);
        System.out.println(add3);

    }

    @Test
    @SneakyThrows
    public void Test() {
        ArrayList<String> arrayString = new ArrayList<String>();
        ArrayList<Integer> arrayInteger = new ArrayList<Integer>();
        System.out.println(arrayString.getClass() == arrayInteger.getClass());// true
        // 这是因为，在编译期间，所有的泛型信息都会被擦除， ArrayList< Integer > 和 ArrayList< String >类型，在编译后都会变成ArrayList<Object>类型。
    }
}
