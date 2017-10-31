package com.lxt.compiler;

import com.squareup.javapoet.CodeBlock;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

class InfuserBinderPool extends BinderPool {

    /**
     * try {
     * Class<?> binderClass = clazz.getClassLoader().loadClass(clazzName + "_ConstructorBinder");
     * String binderClassName = binderClass.toString();
     * Log.d(TAG, "Binder class name " + binderClassName);
     * constructor = (Constructor<? extends Binder>) binderClass.getConstructor(clazz);
     * } catch (ClassNotFoundException e) {
     * e.printStackTrace();
     * Log.e(TAG, " Search super class for constructor " + clazz.getSuperclass());
     * }
     *
     * @param className
     * @param classes
     * @return
     */

    @Override
    CodeBlock generateCode(String className, Class<?>... classes) {
        return CodeBlock.of("try {\n" +
                "Class<?> $NClass = Class.forName(\"$N\");\n" +
                "$NClass.getConstructor();\n" +
                "} catch (Exception e){\n" +
                "e.printStackTrace();\n" +
                "}\n" +
                "object.$N = null;\n", name, className, name, name);
    }
}
