package extense;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 父亲
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-20 11:59
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class Father extends Person {
    /**
     * 儿子
     */
    List<Son> sons = new ArrayList<>();

    public void addSon(Son son) {
        sons.add(son);
    }

    public Father() {
    }

    public Father(String name, String age) {
        super(name, age);
    }

    public Father(String name, String age, Date birthDay, Date deadDay) {
        super(name, age, birthDay, deadDay);
    }

    @Override
    public void eat() {
        System.out.println("Father eat");
    }

    @Override
    public void run() {
        System.out.println("Father run");
    }
}
