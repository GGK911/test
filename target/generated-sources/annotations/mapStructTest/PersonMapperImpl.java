package mapStructTest;

import extense.Person;
import java.text.SimpleDateFormat;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-01T15:56:40+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
public class PersonMapperImpl implements PersonMapper {

    @Override
    public PersonDTO conver(Person person) {
        if ( person == null ) {
            return null;
        }

        PersonDTO personDTO = new PersonDTO();

        personDTO.setPersonName( person.getName() );
        if ( person.getAge() != null ) {
            personDTO.setPersonAge( Integer.parseInt( person.getAge() ) );
        }
        if ( person.getBirthDay() != null ) {
            personDTO.setBirthDay( new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" ).format( person.getBirthDay() ) );
        }
        else {
            personDTO.setBirthDay( "23-11-1 下午3:53" );
        }
        if ( person.getDeadDay() != null ) {
            personDTO.setUpdateTime( person.getDeadDay() );
        }
        else {
            personDTO.setUpdateTime( new java.util.Date() );
        }
        if ( person.getDeadDay() != null ) {
            personDTO.setDeadDay( new SimpleDateFormat().format( person.getDeadDay() ) );
        }

        personDTO.setCreateTime( new java.util.Date() );

        return personDTO;
    }
}
