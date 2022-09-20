package com.kai.waimai;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@SpringBootApplication
@ServletComponentScan
//开启事务注解的支持
@EnableTransactionManagement
public class dakiaiziApplication {
    public static void main(String[] args) {

        SpringApplication.run(dakiaiziApplication.class,args);
        log.info("外卖启动啦");
        log.info("第一次测试");
        //
        //再次进行测试
    }

}
