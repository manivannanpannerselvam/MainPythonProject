package com.prod.tap.exception;

import org.apache.log4j.Logger;
import org.slf4j.helpers.MessageFormatter;


public class TapException extends RuntimeException {
    private static final Logger logger = Logger.getLogger(TapException.class);

    public TapException(TapExceptionType tapExceptionType, String message, Object... args) {
        super(MessageFormatter.arrayFormat(message, args).getMessage());
        logger.info("handling exception " + tapExceptionType);
    }
}
