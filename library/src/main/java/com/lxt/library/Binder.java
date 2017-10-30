package com.lxt.library;

import android.support.annotation.UiThread;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/30.
 */

public interface Binder {

    @UiThread
    void bind();

    Binder BINDER_EMPTY = new Binder() {
        @Override
        public void bind() {

        }
    };
}
