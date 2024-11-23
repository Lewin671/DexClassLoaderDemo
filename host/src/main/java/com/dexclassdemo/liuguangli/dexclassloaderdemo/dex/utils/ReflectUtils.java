package com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectUtils {
    private static final Map<String, Class<?>> sClassCache = new ConcurrentHashMap<>();
    private static final Map<String, Constructor<?>> sConstructorCache = new ConcurrentHashMap<>();
    private static final Map<String, Method> sMethodCache = new ConcurrentHashMap<>();

    public static Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> clazz = sClassCache.get(className);
        if (clazz == null) {
            synchronized (sClassCache) {
                clazz = sClassCache.get(className);
                if (clazz == null) {
                    clazz = Class.forName(className);
                    sClassCache.put(className, clazz);
                }
            }
        }

        return clazz;
    }

    @NonNull
    public static Object newInstance(@NonNull Class<?> clazz, @NonNull Object[] initArgs, @NonNull Class<?>[] parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Constructor<?> constructor = findConstructor(clazz, parameterTypes);
        return constructor.newInstance(initArgs);
    }

    @Nullable
    public static Object callInstanceMethod(@NonNull Object instance, @NonNull String methodName, @NonNull Object[] args, @NonNull Class<?>[] parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = findMethod(instance.getClass(), methodName, parameterTypes);
        return method.invoke(instance, args);
    }

    @Nullable
    public static Object callStaticMethod(@NonNull Class<?> clazz, @NonNull String methodName, @NonNull Object[] args, @NonNull Class<?>[] parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = findMethod(clazz, methodName, parameterTypes);
        return method.invoke(null, args);
    }

    public static Method findMethod(@NonNull Class<?> clazz, @NonNull String methodName, @NonNull Class<?>[] parameterTypes) throws NoSuchMethodException {
        String key = getMethodKey(clazz, methodName, parameterTypes);

        Method method = sMethodCache.get(key);

        if (method == null) {
            synchronized (sMethodCache) {
                method = sMethodCache.get(key);
                if (method == null) {
                    method = clazz.getDeclaredMethod(methodName, parameterTypes);
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                }
                sMethodCache.put(key, method);
            }
        }
        return method;
    }


    @NonNull
    private static Constructor<?> findConstructor(Class<?> clazz, @NonNull Class<?>... parameterTypes) throws NoSuchMethodException {
        String key = getMethodKey(clazz, clazz.getSimpleName(), parameterTypes);
        Constructor<?> constructor = sConstructorCache.get(key);
        if (constructor == null) {
            synchronized (sConstructorCache) {
                constructor = sConstructorCache.get(key);
                if (constructor == null) {
                    constructor = clazz.getDeclaredConstructor(parameterTypes);
                    if (!constructor.isAccessible()) {
                        constructor.setAccessible(true);
                    }
                    sConstructorCache.put(key, constructor);
                }
            }
        }
        return constructor;
    }


    @NonNull
    private static String getMethodKey(@NonNull Class<?> clazz, @NonNull String methodName, @NonNull Class<?>[] parameterTypes) {
        // generate a unique key for the method
        // like className#method#parameterType1-parameterType2-parameterType3-
        StringBuilder key = new StringBuilder();
        key.append(clazz.getName());
        key.append("#");
        key.append(methodName);
        key.append("#");
        for (Class<?> parameterType : parameterTypes) {
            key.append(parameterType.getName());
            key.append("-");
        }
        return key.toString();
    }


}
