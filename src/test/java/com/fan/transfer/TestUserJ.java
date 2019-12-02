package com.fan.transfer;

import com.fan.transfer.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUserJ {
    @Test
    public void testUser1() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        String json = "{\"id\":\"10735069\",\"name\":\"Andrew\",\"email\":\"filyaniny@gmail.com\",\"phone\":\"+35797648671\",\"accounts\":[]}";

        User user = objectMapper.readValue(json, User.class);


        assertThat(user).isNotNull();

    }
}
