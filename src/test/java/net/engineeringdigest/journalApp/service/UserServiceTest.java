package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Disabled
    @Test
    public void testFindByUserName() {
        assertNotNull(userRepository.findByUserName("Saurabh"));
    }

    @ParameterizedTest
    @CsvSource({
            "1,1,3",
            "11,18,-3",
            "111,134,39",
    })
    @Disabled
    public void test(int a, int b, int expected) {
        assertEquals(expected, a + b);
    }
}
