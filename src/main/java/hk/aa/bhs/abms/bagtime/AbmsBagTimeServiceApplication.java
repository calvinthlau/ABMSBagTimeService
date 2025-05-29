package hk.aa.bhs.abms.bagtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AbmsBagTimeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbmsBagTimeServiceApplication.class, args);
	}

}
