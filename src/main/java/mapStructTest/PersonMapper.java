package mapStructTest;

import extense.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-01 15:27
 **/
@Mapper
public interface PersonMapper {

    PersonMapper INSTANCT = Mappers.getMapper(PersonMapper.class);

    @Mapping(target = "personName", source = "name")
    @Mapping(target = "personAge", source = "age")
    @Mapping(target = "birthDay", source = "birthDay", defaultValue = "23-11-1 下午3:53", dateFormat = "yyyy-MM-dd hh:mm:ss")
    @Mapping(target = "updateTime", source = "deadDay", defaultExpression = "java(new java.util.Date())")
    @Mapping(target = "createTime", expression = "java(new java.util.Date())")
    PersonDTO conver(Person person);
}
