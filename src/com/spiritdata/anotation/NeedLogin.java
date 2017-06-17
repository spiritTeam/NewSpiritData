package com.spiritdata.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于判断一个方法是否需要登录才能执行。
 * <pre>
 * 1-此注解是修饰方法的，主要是MVC中的请求处理方法(在controller)中。
 * 2-由于需在AOP中使用，要用到反射，因此此注解需要在运行时有效。
 * </pre>
 * @author wanghui
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedLogin {
}