package com.lxt.compiler;

import com.squareup.javapoet.JavaFile;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/30.
 */

public class ConstructorBinder {

    public String packageName;

    public JavaFile createJavaFile() {
        return JavaFile.builder(packageName, null)
                .build();
    }
}
