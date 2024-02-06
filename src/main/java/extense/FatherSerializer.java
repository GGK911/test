package extense;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Father的序列化器
 * jacksonTest使用
 *
 * @author TangHaoKai
 * @version V1.0 2023-10-23 13:40
 **/
public class FatherSerializer extends StdSerializer<Father> {

    public FatherSerializer(Class<Father> t) {
        super(t);
    }

    @Override
    public void serialize(Father father, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", father.getName());
        jsonGenerator.writeNumberField("age", Long.parseLong(father.getAge()));
        jsonGenerator.writeEndObject();
    }
}
