package bugTest.IDEDebugToStringTest;

/**
 * @author TangHaoKai
 * @version V1.0 2024/5/8 12:15
 */
public class A {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        hasLength(this.name);
        return "A{" +
                "name='" + name + '\'' +
                '}';
    }

    public static boolean hasLength(String str) {
        boolean b = str != null && !str.isEmpty();
        if (!b) {
            throw new IllegalArgumentException("is null!!");
        }
        return b;
    }
}
