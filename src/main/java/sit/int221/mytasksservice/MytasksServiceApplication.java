package sit.int221.mytasksservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MytasksServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MytasksServiceApplication.class, args);
    }

}
