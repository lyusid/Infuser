package com.lxt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface InfuseFloat {

    float[] value();
}
