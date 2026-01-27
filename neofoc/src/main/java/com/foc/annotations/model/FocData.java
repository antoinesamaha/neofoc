package com.foc.annotations.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a class for automatic getter and setter generation.
 * When applied to a class with the 'Foc' prefix, it will look for an Entity
 * with the same name without the 'Foc' prefix and generate getters and setters
 * that match the attribute names in that entity.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface FocData {
}
