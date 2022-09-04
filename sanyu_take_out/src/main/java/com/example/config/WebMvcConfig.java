package com.example.config;

import com.example.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
/**
 * SpringBoot2.x中 WebMvcConfigurerAdapter 已过时，使用WebMvcConfigurationSupport 替换时自动配置失效。
 * https://blog.csdn.net/universsky2015/article/details/108064340?spm=1001.2101.3001.6650.6&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EOPENSEARCH%7ERate-6-108064340-blog-85622112.pc_relevant_default&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EOPENSEARCH%7ERate-6-108064340-blog-85622112.pc_relevant_default&utm_relevant_index=7
 */
//public class WebMvcConfig extends WebMvcConfigurationSupport {
public class WebMvcConfig implements WebMvcConfigurer {


    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        registry.addResourceHandler("/").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
    }

    /**
     * 扩产MVC框架的消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
//        创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
//        设置对象转换器，底层使用Jackson将java对象转为Json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        将上面的消息转换器对象追加到mvc框架的转换器集合中,放到最前方优先使用
        converters.add(0,messageConverter);
    }
}
