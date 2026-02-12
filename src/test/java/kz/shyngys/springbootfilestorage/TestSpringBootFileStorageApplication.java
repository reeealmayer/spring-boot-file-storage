package kz.shyngys.springbootfilestorage;

import org.springframework.boot.SpringApplication;

public class TestSpringBootFileStorageApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringBootFileStorageApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
