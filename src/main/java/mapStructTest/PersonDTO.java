package mapStructTest;

import lombok.Data;

import java.util.Date;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-01 15:23
 **/
@Data
public class PersonDTO {
    String personName;

    int personAge;

    String birthDay;

    String deadDay;

    Date updateTime;

    Date createTime;
}
