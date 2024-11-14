package classTest.genericityTest.interfaceTest;

/**
 * 当去实现 IA 接口时，因为 IA 在继承 IUsu 接口时，指定了类型参数 U 为 String，R 为 Double
 * @author TangHaoKai
 * @version V1.0 2024/10/19 15:17
 */
public class AA implements IA {
    @Override
    public Double get(String s) {
        return Double.parseDouble(s);
    }

    @Override
    public void hello(Double aDouble) {
        System.out.println("AA:hello+" + aDouble);
    }

    @Override
    public Double test(String s) {
        return IA.super.test(s);
    }
}
