package classTest.genericityTest.interfaceTest;

/**
 * 泛型接口
 *
 * @author TangHaoKai
 * @version V1.0 2024/10/19 15:14
 */
public interface IUsb<U, R> {
    int a = 10;

    R get(U u); // 普通方法中，可以使用类型参数

    void hello(R r); // 抽象方法中，可以使用类型参数

    // 在jdk8 中，可以在接口中使用默认方法, 默认方法可以使用泛型接口的类型参数
    default R test(U u) {
        System.out.println("IUsb:test");
        return null;
    }

}
