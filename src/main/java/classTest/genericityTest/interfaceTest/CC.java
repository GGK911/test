package classTest.genericityTest.interfaceTest;

/**
 * 定义一个类 CC 实现了 泛型接口 IUsb 时，若是没有确定泛型接口 IUsb 中的类型参数，则默认为 Object。
 *
 * @author TangHaoKai
 * @version V1.0 2024/10/19 15:25
 */
public class CC implements IUsb { // 等价 class CC implements IUsb<Object, Object>
    @Override
    public Object get(Object o) {
        return o;
    }

    @Override
    public void hello(Object o) {
        System.out.println("CC:hello+" + o);
    }

    @Override
    public Object test(Object o) {
        return IUsb.super.test(o);
    }
}
