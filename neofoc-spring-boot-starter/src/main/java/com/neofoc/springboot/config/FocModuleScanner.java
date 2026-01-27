package com.neofoc.springboot.config;

import com.neofoc.springboot.annotations.FocDeclareModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Component
public class FocModuleScanner {

    @Autowired
    private ApplicationContext applicationContext;

    public void declareFocModulesFromAnnotations() {
        // Get all beans annotated with @FocModule
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(FocDeclareModule.class);

        for (Object bean : beans.values()) {
            try {
                // Call getInstance().declare() using reflection
                Method getInstanceMethod = bean.getClass().getMethod("getInstance");
                Object instance = getInstanceMethod.invoke(null); // Static method
                Method declareMethod = instance.getClass().getMethod("declare");
                declareMethod.invoke(instance);

                System.out.println("Declared module: " + bean.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
