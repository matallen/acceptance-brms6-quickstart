package org.jboss.demo.webapp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class Json {
  public static String toJson(Object o) throws JsonGenerationException, JsonMappingException, IOException{
    if (o==null) return "<null>";
    ObjectMapper mapper = new ObjectMapper();
//    mapper.configure(SerializationFeature.INDENT_OUTPUT,true);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    mapper.writeValue(baos, o);
    return new String(baos.toByteArray());
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <T> T toObject(String payload, Class clazz) throws JsonParseException, JsonMappingException, IOException {
    return (T)new ObjectMapper().readValue(new ByteArrayInputStream(payload.getBytes()), clazz);
  }
}
