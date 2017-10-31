package com.lxt.compiler;

import com.squareup.javapoet.CodeBlock;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

public class InfuserIntBinderPool extends BinderPool {

    int[] value;

    InfuserIntBinderPool(int[] value) {
        this.value = value;
    }

    @Override
    CodeBlock generateCode() {
        StringBuilder sb = new StringBuilder();
        StringBuilder constructorString = new StringBuilder();
        for (int i : value) {
            sb.append(String.format("%s", i)).append(",");
            constructorString.append("Integer.class").append(",");
        }
        String param = sb.substring(0, sb.lastIndexOf(","));
        String conParam = constructorString.substring(0, constructorString.lastIndexOf(","));
        return CodeBlock.of("try {\n" +
                        "\tClass<?> $NClass = Class.forName(\"$N\");\n" +
                        "\t$T<?> constructor = $NClass.getConstructor($N);\n" +
                        "\tobject.$N = ($N)(constructor.newInstance($N));\n" +
                        "} catch (Exception e){\n" +
                        "\te.printStackTrace();\n" +
                        "}\n",
                name, className, NAME_CONSTRUCTOR, name, conParam, name, className, param);
    }
}
