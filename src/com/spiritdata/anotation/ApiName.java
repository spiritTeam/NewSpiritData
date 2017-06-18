package com.spiritdata.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于给方法传递一个名称。
 * <pre>
 * 1-此注解是修饰方法的，主要是MVC中的请求处理方法(在controller)中。
 * 2-由于需在AOP中使用，要用到反射，因此此注解需要在运行时有效。
 * </pre>
 * @author wanghui
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiName {
    /**
     * 得到API的名称
     * @return API的名称
     */
    String value() default "";
}