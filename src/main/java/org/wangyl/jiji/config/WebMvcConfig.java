package org.wangyl.jiji.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.wangyl.jiji.common.JacksonObjectMapper;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Slf4j
@Configuration // 为了说明这是配置类
@EnableSwagger2

public class WebMvcConfig extends WebMvcConfigurationSupport {
    // 设置静态资源映射
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源映射……");
        // addResourceHandler接网页url，addResourceLocations接本地资源位置
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
    }

    //扩展Mvc框架的消息转换器
    //这个转换器底层就有，但是不符合我们的需求，我们自己写了一个转换器，要加入到他们的数组中才统一管理才可以生效
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter =new MappingJackson2HttpMessageConverter();
        //设置对象转换器，使用Jackson将JavaObject转化为Json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器追加到mvc框架的集合中，把我们的放在最前面
        converters.add(0,messageConverter);
    }

}
// 先写好前端，确定后端接口和功能，然后再看数据库表的设计
