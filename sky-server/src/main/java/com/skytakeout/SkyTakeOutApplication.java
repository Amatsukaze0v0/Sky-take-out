package com.skytakeout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@Slf4j
@EnableCaching  //开启Spring Cache缓存注解
@EntityScan(basePackages = "com.skytakeout.entity") // 扫描实体类
@EnableJpaRepositories(basePackages = "com.skytakeout.repository") // 扫描Repository
@ComponentScan(basePackages = {"com.skytakeout", "com.skytakeout.config"})
public class SkyTakeOutApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkyTakeOutApplication.class, args);
        log.info("server started");
    }
}