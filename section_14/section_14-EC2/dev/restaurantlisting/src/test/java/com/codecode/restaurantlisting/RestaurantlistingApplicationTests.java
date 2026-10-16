package com.codecode.restaurantlisting;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
@ActiveProfiles("test") //use a different database for testing instead of using prod database
class RestaurantlistingApplicationTests {

    @Test
    void contextLoad() {
        assertTrue(true);

    }

}
