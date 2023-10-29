package com.ruyi.mallchat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author <a href="https://github.com/djc-github">ruyi</a>
 * Date: 2023-10-02
 */
@SpringBootApplication(scanBasePackages = {"com.ruyi"})
@MapperScan({"com.ruyi.mallchat.**.mapper"})
@ServletComponentScan
public class MallchatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallchatCustomApplication.class, args);
    }

}