package me.sensingself.sensingsugar.Common.util;

/**
 * Created by liujie on 2/8/18.
 */

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
    public String value() default "";

    //used for Data;
    public String format() default "yyyy-MM-dd HH:mm:ss"  ; //2013-12-22 20:30:35

    public boolean ignoreGet() default false;  // means when obj to json, ingore the field
    public boolean ignoreSet() default false;   // when json to obj, don't set the field

}
