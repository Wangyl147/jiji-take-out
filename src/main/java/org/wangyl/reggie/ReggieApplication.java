package org.wangyl.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j // 日志
@SpringBootApplication // SpringBoot的启动类
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功！");
    }
}

// 前端静态资源只有放在static和template目录才能被访问到
// 需要一个配置类，让静态资源能够访问到