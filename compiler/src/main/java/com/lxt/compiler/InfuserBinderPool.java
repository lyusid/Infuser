package com.lxt.compiler;

import com.squareup.javapoet.CodeBlock;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

class InfuserBinderPool extends BinderPool {

    @Override
    CodeBlock generateCode() {
        return CodeBlock.of("object.$N = null;", name);
    }
}
