package com.lxt.compiler;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

abstract class BinderPool {

    static final ClassName NAME_CONSTRUCTOR = ClassName.get("java.lang.reflect", "Constructor");

    Type type = Type.EMTPY;

    String name;

    String packageName;

    String className;

    private BinderPool bindPool(String name, String packageName, String className, Type type) {
        this.name = name;
        this.packageName = packageName;
        this.type = type;
        this.className = className;
        return this;
    }

    BinderPool build(Element element) {
        VariableElement variableElement = (VariableElement) element;
        String className = TypeName.get(variableElement.asType()).toString();
        String name = element.getSimpleName().toString();
        String packageName = MoreElements.getPackage(element).getQualifiedName().toString();
        return bindPool(name, packageName, className, type);
    }

    abstract CodeBlock generateCode();

}
