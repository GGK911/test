package extense;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 儿子
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-20 12:00
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class Son extends Person {
    /**
     * 父亲
     */
    Father father;

    public Son() {
    }

    public Son(String name, String age) {
        super(name, age);
    }

    @Override
    public void eat() {
        System.out.println("Son eat");
    }

    @Override
    public void run() {
        System.out.println("Son run");
    }
}
