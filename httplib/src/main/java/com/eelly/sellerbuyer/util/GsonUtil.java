package com.eelly.sellerbuyer.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 仿照Gson的解析工具<br>
 * <br>
 * Gson会忽略transient字段，此工具不会<br>
 * 增加了@DeSerializeNames注解，以对应多个json名字的情况<br>
 * 反序列化名优先级 @DeSerializeNames > @SerializedName > 本名<br>
 * 
 * @author 苏腾
 */
public class GsonUtil {

	/**
	 * Json反序列化名称数组
	 * 
	 * @author 苏腾
	 */
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface DeSerializeNames {

		/**
		 * 名称数组
		 * 
		 * @return
		 */
		String[] values();

	}

	/**
	 * 由json解析对象
	 * 
	 * @param object 待解析对象
	 * @param jo json对象
	 * @param gson gson对象
	 * @throws Exception
	 */
	public static void parse(Object object, JsonObject jo, Gson gson) throws Exception {
		ArrayList<Field> allFields = new ArrayList<>();
		Class<?> cls = object.getClass();
		while (cls != Object.class) {
			Field[] fields = cls.getDeclaredFields();
			if (fields != null) {
				allFields.addAll(Arrays.asList(fields));
			}
			cls = cls.getSuperclass();
		}

		for (Field field : allFields) {
			int modifers = field.getModifiers();
			if ((modifers & Modifier.FINAL) > 0 || (modifers & Modifier.STATIC) > 0) { // 忽略final和static字段
				continue;
			}
			Expose expose = field.getAnnotation(Expose.class);
			if (expose != null && !expose.deserialize()) { // 忽略Expose注解指定的deserialize=false字段
				continue;
			}
			JsonElement fieldValue = null; // 字段值
			DeSerializeNames dsn = field.getAnnotation(DeSerializeNames.class); // 如果有DeSerializeNames注解，尝试其值
			if (dsn != null) {
				for (String name : dsn.values()) {
					fieldValue = jo.get(name);
					if (fieldValue != null)
						break;
				}
			}
			if (fieldValue == null) {
				String fieldName = null; // 反序列化名
				SerializedName sn = field.getAnnotation(SerializedName.class);
				if (sn != null) {
					fieldName = sn.value(); // 如果有SerializedName注解，取其值
				} else {
					fieldName = field.getName(); // 取值为字段名
				}
				fieldValue = jo.get(fieldName);
			}
			if (fieldValue != null) {
				Object value = gson.fromJson(fieldValue, field.getType()); // 反射出真正的数值类型
				field.setAccessible(true);
				field.set(object, value); // 赋值
			}
		}
	}
}
