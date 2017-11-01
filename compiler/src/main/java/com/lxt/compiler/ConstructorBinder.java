package com.lxt.compiler;

import com.google.auto.common.MoreElements;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    private Map<BinderPool, ClassName> binderPoolMap = new HashMap<>();

    private boolean isFinal;

    ConstructorBinder(Map<BinderPool, ClassName> map, TypeName typeName, ClassName className, boolean isFinal) {
        this.binderPoolMap = map;
        this.typeName = typeName;
        this.className = className;
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
        Set<Map.Entry<BinderPool, ClassName>> entries = binderPoolMap.entrySet();
        for (Map.Entry<BinderPool, ClassName> entry : entries) {
            ClassName value = entry.getValue();
            if (value.equals(className))
                builder.addCode(entry.getKey().generateCode());
        }
        return builder.build();
    }

    static class Builder {

        private static Map<BinderPool, ClassName> binderPoolMap = new HashMap<>();

        private TypeName typeName;

        private ClassName className;

        private boolean isFinal;

        Builder(TypeName typeName, ClassName className, boolean isFinal) {
            this.typeName = typeName;
            this.className = className;
            this.isFinal = isFinal;
        }

        void addBinderPool(BinderPool binderPool) {
            binderPoolMap.put(binderPool, className);
        }

        ConstructorBinder build() {
            return new ConstructorBinder(binderPoolMap, typeName, className, isFinal);
        }
    }
}
