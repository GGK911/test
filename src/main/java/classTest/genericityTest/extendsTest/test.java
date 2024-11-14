package classTest.genericityTest.extendsTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 通配符测试
 *
 * @author TangHaoKai
 * @version V1.0 2024/10/21 9:26
 */
public class test {
    @Test
    @SneakyThrows
    public void extendsTest() {
        // ArrayList<Number> numberArrayList = new ArrayList<Integer>(); 编译报错
        // 上界通配符 <? extends T>
        ArrayList<? extends Number> numberArrayList = new ArrayList<Integer>(); // 编译正确
        numberArrayList = new ArrayList<Float>();
        numberArrayList = new ArrayList<Double>();
        numberArrayList = new ArrayList<Long>();
        numberArrayList = new ArrayList<Number>();

        // numberArrayList.add(new Integer(1)); 编译报错

        // ArrayList<? extends Number> numberArrayList = new ArrayList<Number>();
        // 和
        // ArrayList<Number> numberArrayList = new ArrayList<>();
        // 并不等价

        // 使用 extends 通配符表示可以读，不能写。
    }

    @Test
    @SneakyThrows
    public void superTest() {
        // ArrayList<Integer> list01 = new ArrayList<Number>(); 编译错误
        // T 代表了类型参数的下界，<? super T>表示类型参数的范围是 T 和 T 的超类，直至 Object。
        ArrayList<? super Integer> list02 = new ArrayList<Integer>();// 编译正确
        list02 = new ArrayList<Number>();
        list02 = new ArrayList<Object>();

        // 下面是需要理解的地方
        ArrayList<? super Number> list = new ArrayList<>();

        list.add(new Integer(1));// 编译正确
        list.add(new Float(1.0));// 编译正确
        list.add((Number) new Float(1.0));// 编译正确

        // Object 是 Number 的父类
        // list.add(new Object()); 编译错误

        // 这里奇怪的地方出现了，为什么和ArrayList<? extends Number> 集合不同， ArrayList<? super Number> 集合中可以添加 Number 类及其子类的对象呢？
        // 其原因是， ArrayList<? super Number> 的下界是 ArrayList< Number > 。
        // 因此，我们可以确定 Number 类及其子类的对象自然可以加入 ArrayList<? super Number> 集合中；
        // 而 Number 类的父类对象就不能加入 ArrayList<? super Number> 集合中了，因为不能确定 ArrayList<? super Number> 集合的数据类型。
        // 简而言之，就是父类是谁我不确定，可能是Number、Object；但是可以确定 Number 类及其子类我能添加进去，

        // 使用 super 通配符表示可以写，不能读。
    }

    @Test
    @SneakyThrows
    public void Test() {
        // Java 中 List<Object> 和原始类型 List 之间不等价
        // 创建一个 ArrayList<String> 集合
        List<Object> list = new ArrayList<>();
        List list2 = new ArrayList<>();

        // list = new ArrayList<String>(); 编译错误
        list2 = new ArrayList<String>();// 编译正确

    }
}
