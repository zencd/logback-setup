package com.github.zencd.logbacksetup;

import ch.qos.logback.core.AppenderBase;

public class MyAppender<E> extends AppenderBase<E> {
    @Override
    protected void append(E eventObject) {

    }
}
