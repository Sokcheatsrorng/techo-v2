package co.istad.techco.techco;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://202.178.125.77:44443/ishop_db",
        "spring.datasource.username=postgres",
        "spring.datasource.password=1234567890"
})
@ActiveProfiles("dev")
class TechCoApplicationTests {

    @Test
    void contextLoads() {
    }
}

