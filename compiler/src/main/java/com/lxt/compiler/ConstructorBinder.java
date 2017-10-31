package com.lxt.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/30.
 */

class ConstructorBinder {

    private static final ClassName MAIN_THREAD = ClassName.get("android.support.annotation", "UiThread");

    private TypeName typeName;

    private ClassName className;

    private boolean isFinal;

    ConstructorBinder(TypeName typeName, ClassName className, boolean isFinal) {
        this.typeName = typeName;
        this.className = className;
        this.isFinal = isFinal;
    }

    static Builder builder(TypeElement enclosingElement, Elements elements) {
        TypeName typeName = TypeName.get(enclosingElement.asType());
        if (typeName instanceof ParameterizedTypeName) {
            typeName = ((ParameterizedTypeName) typeName).rawType;
        }
        String packageName = elements.getPackageOf(enclosingElement).getQualifiedName().toString();
        String className = enclosingElement.getQualifiedName().toString()
                .substring(packageName.length() + 1).replace('.', '$');
        ClassName binderClassName = ClassName.get(packageName, className + Suffix.CONSTRUCTOR_BINDER);
        boolean isFinal = enclosingElement.getModifiers().contains(Modifier.FINAL);
        return new Builder(typeName, binderClassName, isFinal);
    }

    JavaFile createJavaFile() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(className.simpleName())
                .addModifiers(Modifier.PUBLIC);
        if (isFinal)
            builder.addModifiers(Modifier.FINAL);
        builder.addMethod(createConstructor());
        return JavaFile.builder(className.packageName(), builder.build())
                .build();
    }

    private MethodSpec createConstructor() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addAnnotation(MAIN_THREAD)
                .addModifiers(Modifier.PUBLIC);

        builder.addParameter(typeName, "object");

        return builder.build();
    }

    static class Builder {

        private TypeName typeName;

        private ClassName className;

        private boolean isFinal;

        Builder(TypeName typeName, ClassName className, boolean isFinal) {
            this.typeName = typeName;
            this.className = className;
            this.isFinal = isFinal;
        }

        ConstructorBinder build() {
            return new ConstructorBinder(typeName, className, isFinal);
        }
    }
}
