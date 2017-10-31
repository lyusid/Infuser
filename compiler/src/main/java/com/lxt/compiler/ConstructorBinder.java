package com.lxt.compiler;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.lxt.compiler.Suffix.PACKAGE_LINRARY;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/30.
 */

class ConstructorBinder {

    private static final ClassName MAIN_THREAD = ClassName.get("android.support.annotation", "UiThread");

    private static final ClassName INTERFACE_BINDER = ClassName.get(PACKAGE_LINRARY, "Binder");

    private TypeName typeName;

    private ClassName className;

    private List<BinderPool> binderPools = new LinkedList<>();

    private boolean isFinal;


    ConstructorBinder(TypeName typeName, ClassName className, List<BinderPool> binderPools, boolean isFinal) {
        this.typeName = typeName;
        this.className = className;
        this.binderPools = binderPools;
        this.isFinal = isFinal;
    }

    static Builder builder(TypeElement enclosingElement) {
        TypeName typeName = TypeName.get(enclosingElement.asType());
        if (typeName instanceof ParameterizedTypeName) {
            typeName = ((ParameterizedTypeName) typeName).rawType;
        }
        String packageName = MoreElements.getPackage(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString()
                .substring(packageName.length() + 1).replace('.', '$');
        ClassName binderClassName = ClassName.get(packageName, className + Suffix.CONSTRUCTOR_BINDER);
        boolean isFinal = enclosingElement.getModifiers().contains(Modifier.FINAL);
        return new Builder(typeName, binderClassName, isFinal);
    }

    JavaFile createJavaFile() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(className.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(INTERFACE_BINDER);
        if (isFinal)
            builder.addModifiers(Modifier.FINAL);
        builder.addMethod(createConstructor())
                .addMethod(overrideBind());
        return JavaFile.builder(className.packageName(), builder.build())
                .build();
    }

    private MethodSpec overrideBind() {
        return MethodSpec.methodBuilder("bind")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.BOOLEAN)
                .addStatement("return bound")
                .build();
    }

    private MethodSpec createConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addAnnotation(MAIN_THREAD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(typeName, "object");
        for (BinderPool binderPool : binderPools) {
            builder.addCode(binderPool.generateCode());
        }
        return builder.build();
    }

    static class Builder {

        private static List<BinderPool> binderPools = new CopyOnWriteArrayList<>();

        private TypeName typeName;

        private ClassName className;

        private boolean isFinal;

        Builder(TypeName typeName, ClassName className, boolean isFinal) {
            this.typeName = typeName;
            this.className = className;
            this.isFinal = isFinal;
        }

        void addBinderPool(BinderPool binderPool) {
            binderPools.add(binderPool);
        }

        ConstructorBinder build() {
            return new ConstructorBinder(typeName, className, binderPools, isFinal);
        }
    }
}
