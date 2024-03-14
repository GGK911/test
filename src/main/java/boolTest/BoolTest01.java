package boolTest;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author TangHaoKai
 * @version V1.0 2023-12-21 15:41
 **/
public class BoolTest01 {

    public static void main(String[] args) {
        System.out.println(Boolean.parseBoolean("123"));
    }

    boolean a;
    int b;
    float c;
    long d;
    short e;
    char f;
    byte g;

    @Test
    @SneakyThrows
    public void defaultValue() {
        // System.out.println(a);
        // System.out.println(b);
        // System.out.println(c);
        // System.out.println(d);
        // System.out.println(e);
        // System.out.println(f);
        // System.out.println(g);

        Boolean h = false;
        if (h != null) {
            System.out.println("1");
        }
    }
}
