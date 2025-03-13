package com.skytakeout.context;

public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentID(long id) {threadLocal.set(id);}

    public static Long getCurrentID() {return threadLocal.get();}

    public static void removeCurrentID() {threadLocal.remove();}
}
