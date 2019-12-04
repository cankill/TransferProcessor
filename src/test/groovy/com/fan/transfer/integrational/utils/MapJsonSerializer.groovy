package com.fan.transfer.integrational.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class MapJsonSerializer extends JsonSerializer<LinkedHashMap> {
    @Override
    void serialize(LinkedHashMap map, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject()
        map.each{k,v ->
            jgen.writeObjectField((String) k, v)
        }
        jgen.writeEndObject();
    }
}
