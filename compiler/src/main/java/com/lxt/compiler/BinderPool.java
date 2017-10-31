package com.lxt.compiler;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.Element;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

abstract class BinderPool {

    Type type;

    String name;

    String packageName;

    private BinderPool bindPool(String name, String packageName, Type type) {
        this.name = name;
        this.packageName = packageName;
        this.type = type;
        return this;
    }

    BinderPool build(Element element, Type type) {
        return bindPool(element.getSimpleName().toString(),
                MoreElements.getPackage(element).getQualifiedName().toString(), type);
    }

    abstract CodeBlock generateCode(String className,Class<?>... classes);

}
