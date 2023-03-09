package org.wangyl.reggie.common;

//基于ThreadLocal封装工具类，用于保存和获取当前用户id
//具有线程隔离的效果，在某个线程里set的变量也必须在同一个线程中get到
// ThreadLocal不是线程，而是线程的局部变量。使用ThreadLocal维护变量时，使用这个变量的所有线程都有一个独立的副本，每个线程只能独立地改变自己的副本，而不能改变其他线程的副本。
// ThreadLocal为每个线程单独提供一份存储空间，具有线程隔离的效果，只有在线程内才能获取对应的值，线程外则不能访问。
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
