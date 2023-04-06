package org.wangyl.jiji;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j // 日志
@SpringBootApplication // SpringBoot的启动类
@ServletComponentScan //为了让启动时能够扫描组件，找到filter
@EnableTransactionManagement // 事务支持
@EnableCaching
@EnableAspectJAutoProxy
public class JijiApplication {
    public static void main(String[] args) {
        SpringApplication.run(JijiApplication.class,args);
        log.info("项目启动成功！");
    }
}

// 前端静态资源只有放在static和template目录才能被访问到
// 需要一个配置类，让静态资源能够访问到