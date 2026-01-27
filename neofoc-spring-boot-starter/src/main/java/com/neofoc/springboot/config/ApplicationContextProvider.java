package com.neofoc.springboot.config;

import com.foc.ConfigInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ConfigInfo.IApplicationYmlConfig {
    private static ApplicationContext context;

    public ApplicationContextProvider(ApplicationContext applicationContext) {
        ApplicationContextProvider.context = applicationContext;
    }

    @PostConstruct
    public void init() {
        // Set the implementation in ConfigInfo
        ConfigInfo.setApplicationYmlConfig(this);
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        return context.getEnvironment().getProperty(key, targetType);
    }

    public String getProperty(String key) {
        return context.getEnvironment().getProperty(key);
    }
}
