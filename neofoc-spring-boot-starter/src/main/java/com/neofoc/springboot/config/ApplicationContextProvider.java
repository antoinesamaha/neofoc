package com.neofoc.springboot.config;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider {
    private static ApplicationContext context;

    public ApplicationContextProvider(ApplicationContext applicationContext) {
        ApplicationContextProvider.context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }
}
