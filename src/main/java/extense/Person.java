package extense;

import lombok.Data;

import java.util.Date;

/**
 * 人类
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-20 12:00
 **/
@Data
public class Person implements Animal {
    String name;

    String age;

    Date birthDay;

    Date deadDay;

    public Person() {
    }

    public Person(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, String age, Date birthDay, Date deadDay) {
        this.name = name;
        this.age = age;
        this.birthDay = birthDay;
        this.deadDay = deadDay;
    }

    @Override
    public void eat() {

    }

    @Override
    public void run() {

    }
}
