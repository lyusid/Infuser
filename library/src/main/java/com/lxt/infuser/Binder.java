package com.lxt.infuser;

import android.support.annotation.UiThread;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/30.
 */

public interface Binder {

    boolean bound = false;

    @UiThread
    boolean bind();

    Binder BINDER_EMPTY = new Binder() {
        @Override
        public boolean bind() {
            return bound;
        }
    };
}
