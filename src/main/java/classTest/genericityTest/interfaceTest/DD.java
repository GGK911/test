package classTest.genericityTest.interfaceTest;

/**
 * 定义一个类 DD 实现了 泛型接口 IUsb 时，若是没有确定泛型接口 IUsb 中的类型参数，也可以将 DD 类也定义为泛型类，其声明的类型参数必须要和接口 IUsb 中的类型参数相同。
 *
 * @author TangHaoKai
 * @version V1.0 2024/10/19 15:29
 */
public class DD<U, R> implements IUsb<U, R> {
    @Override
    public R get(U u) {

        return null;
    }

    @Override
    public void hello(R r) {

    }

    @Override
    public R test(U u) {
        return IUsb.super.test(u);
    }
}
