package com.lxt.compiler;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/26
 */

class ModifierException extends RuntimeException {

    ModifierException(Object object, String className) {
        super(String.format("The modifier of %s in %s should be public", object, className));
    }

    static void printException(Object object, String className) {
        throw new ModifierException(object, className);
    }
}
