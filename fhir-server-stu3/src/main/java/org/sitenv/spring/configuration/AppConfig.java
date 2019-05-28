package org.sitenv.spring.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
//@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = "org.sitenv.spring")
public class AppConfig extends WebMvcConfigurerAdapter {

}
