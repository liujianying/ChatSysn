package com.eelly.framework.app;

/**
 * 实现该接口的类不参与代码混淆<br>
 * 通常用于使用反射的类，实现该接口则不会被改名、不会被删掉无直接引用的方法和属性<br>
 * <br>
 * 在proguard配置文件里加入以下语句以实现：<br>
 * -keep interface com.eelly.framework.app.NonProguard <br>
 * -keep class * extends com.eelly.framework.app.NonProguard { *; }
 * 
 * @author 苏腾
 */
public interface NonProguard {

}
