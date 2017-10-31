package com.lxt.infuser;

import com.lxt.annotation.Infuse;
import com.lxt.annotation.InfuseInt;

/**
 * @author lxt <lxt352@gmail.com>
 * @since 2017/10/31.
 */

public class TestClass {

    @Infuse
    public String test;

    @InfuseInt({1, 2})
    public Singer singer;
}
