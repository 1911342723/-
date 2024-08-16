package com.java_project.reggie.common;


public class BaseContext {

    //创建一个静态的ThreadLocal类
    private static ThreadLocal<Long>  threadLocal = new ThreadLocal<>();

    //创建两个方法
    public static void setThreadLocal(Long id){
        threadLocal.set(id);
    }

    public static Long getThreadLocal(){
        return threadLocal.get();
    }


}
