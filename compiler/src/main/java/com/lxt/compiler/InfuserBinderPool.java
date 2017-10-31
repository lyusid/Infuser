package com.lxt.compiler;

import com.squareup.javapoet.CodeBlock;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

class InfuserBinderPool extends BinderPool {

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
