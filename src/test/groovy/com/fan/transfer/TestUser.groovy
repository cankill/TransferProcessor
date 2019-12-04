package com.fan.transfer

import com.fan.transfer.domain.User
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class TestUser extends Specification {
    def testUser() {
        setup:
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
        def createdUser = objectMapper.readValue(jsonString, User.class)

        expect:
        createdUser == expectedUser

        where:
        expectedUser << [
                User.builder()
                        .id(User.Id.builder().value("10735069").build())
                        .name("Andrew")
                        .email("filyaniny@gmail.com")
                        .phone("+35797648671")
                        .build()
        ]
    }
}
