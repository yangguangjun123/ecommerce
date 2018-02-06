package org.myproject.ecommerce.core.utilities;

import org.slf4j.Logger;

public class LoggingUtils {
    public static void info(Logger logger, String msg) {
        if(logger.isInfoEnabled()) {
            logger.info(msg);
        }
    }
}
