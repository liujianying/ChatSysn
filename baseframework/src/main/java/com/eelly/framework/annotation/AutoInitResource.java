package com.eelly.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动注入资源注解<br>
 * <br>
 * 在proguard配置文件里加入以下语句以防止字段被删除<br>
 * -keepclassmembers class * { @com.eelly.framework.annotation.AutoInitResource *; }
 * 
 * @author 李欣
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoInitResource {

	/**
	 * 资源id
	 * 
	 * @return
	 */
	int id();

	/**
	 * 资源类型
	 * 
	 * @return
	 */
	ResourceType type();
}
