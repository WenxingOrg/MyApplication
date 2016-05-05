package com.example.wzhao1.myapplication.util.navigator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link Navigator} will check if the user is authenticated
 * before executing the action if the target class fo the navigation contains this annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticationRequired {
}
