package com.frms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.frms.mapper")
@ServletComponentScan("com.frms.filter")
public class FrmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrmsApplication.class, args);
    }

}
