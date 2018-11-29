package org.medvedev.nikita.services;

import org.junit.Test;
import org.medvedev.nikita.objects.UserData;

public class TokenTest {
    @Test
    public void shouldGetCorrectToken()
    {
        //Given
        UserData userData = new UserData();
        userData.setLogin("nikita");
        userData.setFirstName("Nikita");
        userData.setSecondName("Medvedev");
        userData.setEmail("nikita@medvedev.ru");
        //When
        String token = Utils.generateToken(userData);
        //Then
        System.out.println(token);
    }
}
