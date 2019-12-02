package com.fan.transfer

import com.fan.transfer.domain.User
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.testng.annotations.Test
import static org.assertj.core.api.Assertions.*;

class TestUser {

    @Test
    public void testUser() {
        def jsonString = '''
                    {
                        "id": "10735069",
                        "name": "Andrew",
                        "email": "filyaniny@gmail.com",
                        "phone": "+35797648671",
                        "accounts": []                        
                    }
                    '''
        def objectMapper = new ObjectMapper()
//        objectMapper.findAndRegisterModules();

        def user = objectMapper.readValue(jsonString, User.class)

        assertThat(user).isNotNull()


    }
}
