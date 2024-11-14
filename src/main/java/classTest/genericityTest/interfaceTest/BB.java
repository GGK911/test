package classTest.genericityTest.interfaceTest;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * 实现接口时，需要指定泛型接口的类型参数
 * 给 U 指定 Integer， 给 R 指定了 Float
 * 所以，当我们实现 IUsb 方法时，会使用 Integer 替换 U, 使用 Float 替换 R
 *
 * @author TangHaoKai
 * @version V1.0 2024/10/19 15:21
 */
public class BB implements IUsb<String, Date> {
    @Override
    public Date get(String s) {
        return DateUtil.parse(s);
    }

    @Override
    public void hello(Date date) {
        System.out.println("BB:hello+" + date);
    }

    @Override
    public Date test(String s) {
        return IUsb.super.test(s);
    }
}
