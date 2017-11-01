package com.lxt.compiler;

import com.squareup.javapoet.CodeBlock;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

class InfuserArrayBinderPool extends BinderPool {

    private Type type;

    private int[] intValues;

    private long[] longValues;

    private float[] floatValues;

    private double[] doubleValues;

    private char[] charValues;

    private String[] stringValues;

    InfuserArrayBinderPool(Type type, Object object) {
        this.type = type;
        switch (type) {
            case INT:
                intValues = (int[]) object;
                break;
            case LONG:
                longValues = (long[]) object;
                break;
            case FLOAT:
                floatValues = (float[]) object;
                break;
            case DOUBLE:
                doubleValues = (double[]) object;
                break;
            case CHAR:
                charValues = (char[]) object;
                break;
            case STRING:
                stringValues = (String[]) object;
                break;
        }
    }

    @Override
    CodeBlock generateCode() {
        StringBuilder realString = new StringBuilder();
        StringBuilder constructorString = new StringBuilder();
        switch (type) {
            case INT:
                for (int i : intValues) {
                    realString.append(String.format("%s", i)).append(",");
                    constructorString.append("int.class").append(",");
                }
                break;
            case LONG:
                for (long i : longValues) {
                    realString.append(String.format("%sL", i)).append(",");
                    constructorString.append("long.class").append(",");
                }
                break;
            case FLOAT:
                for (float i : floatValues) {
                    realString.append(String.format("%sf", i)).append(",");
                    constructorString.append("float.class").append(",");
                }
                break;
            case DOUBLE:
                for (double i : doubleValues) {
                    realString.append(String.format("%s", i)).append(",");
                    constructorString.append("double.class").append(",");
                }
                break;
            case CHAR:
                for (char i : charValues) {
                    realString.append(String.format("'%s'", i)).append(",");
                    constructorString.append("char.class").append(",");
                }
                break;
            case STRING:
                for (String i : stringValues) {
                    realString.append(String.format("\"%s\"", i)).append(",");
                    constructorString.append("String.class").append(",");
                }
                break;
        }
        String realParam = realString.substring(0, realString.lastIndexOf(","));
        String conParam = constructorString.substring(0, constructorString.lastIndexOf(","));
        return CodeBlock.of("try {\n" +
                        "\tClass<?> $NClass = Class.forName(\"$N\");\n" +
                        "\t$T<?> constructor = $NClass.getConstructor($N);\n" +
                        "\tobject.$N = ($N)(constructor.newInstance($N));\n" +
                        "} catch (Exception e){\n" +
                        "\te.printStackTrace();\n" +
                        "}\n",
                name, className,
                NAME_CONSTRUCTOR, name, conParam,
                name, className, realParam);
    }
}
