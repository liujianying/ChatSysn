package com.eelly.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注入视图注解<br>
 * <br>
 * 在proguard配置文件里加入以下语句以防止字段被删除<br>
 * -keepclassmembers class * { @com.eelly.framework.annotation.AutoInitView *; }
 * 
 * @author 李欣,苏腾
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoInitView {

	/**
	 * 视图id
	 * 
	 * @return
	 */
	int id();

	/**
	 * setOnClickListener(Object)
	 */
	boolean click() default false;

	/**
	 * setOnLongClickListener(Object)
	 */
	boolean longclick() default false;

	/**
	 * setOnTouchListener(Object)
	 */
	boolean touch() default false;

	/**
	 * setOnKeyListener(Object)
	 */
	boolean key() default false;

	/**
	 * setOnFocusChangeListener(Object)
	 */
	boolean focuschange() default false;

	/**
	 * setOnCreateContextMenuListener(Object)
	 */
	boolean contextmenu() default false;

}
