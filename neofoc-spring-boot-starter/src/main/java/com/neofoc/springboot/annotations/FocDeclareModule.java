package com.neofoc.springboot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // Can be applied to classes or interfaces
@Retention(RetentionPolicy.RUNTIME) // Retained at runtime for reflection
public @interface FocDeclareModule {

}
