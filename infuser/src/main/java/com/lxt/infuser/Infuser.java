package com.lxt.infuser;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;

import java.lang.reflect.Constructor;
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
        return null;
    }

    private static Constructor<? extends Binder> findBinderConstructorForClass(Class<?> clazz) {
        Constructor<? extends Binder> constructor = BINDERS.get(clazz);
        if (constructor != null) {
            Log.d(TAG,"Cached binder in binding map.");
        }
        return null;
    }
}
