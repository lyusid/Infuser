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
     */
    @Override
    CodeBlock generateCode() {
        return CodeBlock.of("try {\n" +
                        "\tClass<?> $NClass = Class.forName(\"$N\");\n" +
                        "\t$T<?> constructor = $NClass.getConstructor();\n" +
                        "\tobject.$N = ($N)(constructor.newInstance());\n" +
                        "} catch (Exception e){\n" +
                        "\te.printStackTrace();\n" +
                        "}\n",
                name, className, NAME_CONSTRUCTOR, name, name, className);
    }
}
