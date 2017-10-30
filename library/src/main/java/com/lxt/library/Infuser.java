package com.lxt.library;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.acl.LastOwnerException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Infuser {

    private static final String TAG = Infuser.class.getSimpleName();

    private static final Map<Class<?>, Constructor<? extends Binder>> BINDERS = new LinkedHashMap<>();

    @NonNull
    @UiThread
    public static Binder bind(Object object) {
        return createBinder(object);
    }

    private static Binder createBinder(Object object) {
        Class<?> clazz = object.getClass();
        Constructor<? extends Binder> constructor = findBinderConstructorForClass(clazz);
        if (constructor == null)
            return Binder.BINDER_EMPTY;
        try {
            return constructor.newInstance(object);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create new instance " + constructor, e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create new instance " + constructor, e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create new instance " + constructor, e);
        }
    }

    private static Constructor<? extends Binder> findBinderConstructorForClass(Class<?> clazz) {
        Constructor<? extends Binder> constructor = BINDERS.get(clazz);
        if (constructor != null) {
            Log.d(TAG, "Cached binder in binding map.");
            return constructor;
        }
        String clazzName = clazz.getName();
        try {
            Class<?> binderClass = clazz.getClassLoader().loadClass(clazzName + "_ConstructorBinder");
            String binderClassName = binderClass.toString();
            Log.d(TAG, "Binder class name " + binderClassName);
            constructor = (Constructor<? extends Binder>) binderClass.getConstructor(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            constructor = findBinderConstructorForClass(clazz.getSuperclass());
            Log.e(TAG, " Search super class for constructor " + clazz.getSuperclass());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("No constuctor is found", e);
        }
        BINDERS.put(clazz, constructor);
        return constructor;
    }

}
