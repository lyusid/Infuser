package com.lxt.compiler;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/26
 */

class ModifierException extends Exception {

    private ModifierException(Object object) {
        super(String.format("The modifier of %s should be public", object));
    }

    static void printException(Object object) {
        new ModifierException(object).printStackTrace();
    }
}
