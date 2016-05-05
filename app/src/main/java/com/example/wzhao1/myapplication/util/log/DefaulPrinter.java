package com.example.wzhao1.myapplication.util.log;

import java.util.logging.Level;
import java.util.logging.Logger;

/** Default Log printer that uses java standard {@link Logger}.
 * @author gerardo.bercovich
 */
public final class DefaulPrinter implements LogPrinter {
    private static final Logger LOGGER = Logger.getLogger(DefaulPrinter.class.getName());

    @Override
    public int v(String message, Object... args) {
        LOGGER.log(Level.FINEST, formatArgs(message, args));
        return 0;
    }

    @Override
    public int v(Throwable throwable, String message, Object... args) {
        LOGGER.log(Level.FINEST, formatArgs(message, args), throwable);
        return 0;
    }

    @Override
    public int d(String message, Object... args) {
        LOGGER.log(Level.FINER, formatArgs(message, args));
        return 0;
    }

    @Override
    public int d(Throwable throwable, String message, Object... args) {
        LOGGER.log(Level.FINER, formatArgs(message, args), throwable);
        return 0;
    }

    @Override
    public int i(Throwable throwable, String message, Object... args) {
        LOGGER.log(Level.INFO, formatArgs(message, args), throwable);
        return 0;
    }

    @Override
    public int i(String message, Object... args) {
        LOGGER.log(Level.INFO, formatArgs(message, args));
        return 0;
    }

    @Override
    public int w(Throwable throwable, String message, Object... args) {
        LOGGER.log(Level.WARNING, formatArgs(message, args), throwable);
        return 0;
    }

    @Override
    public int w(String message, Object... args) {
        LOGGER.log(Level.WARNING, formatArgs(message, args));
        return 0;
    }

    @Override
    public int e(Throwable throwable, String message, Object... args) {
        LOGGER.log(Level.SEVERE, formatArgs(message, args), throwable);
        return 0;
    }

    @Override
    public int e(String message, Object... args) {
        LOGGER.log(Level.SEVERE, formatArgs(message, args));
        return 0;
    }

    @Override
    public boolean isDebugEnabled() {
        return LOGGER.isLoggable(Level.FINER);
    }

    @Override
    public boolean isVerboseEnabled() {
        return LOGGER.isLoggable(Level.FINEST);
    }

    @Override
    public int getLoggingLevel() {
        throw new UnsupportedOperationException("getLoggingLevel is not supported in this implementation.");
    }

    @Override
    public void setLoggingLevel(int level) {
        throw new UnsupportedOperationException(
                "This implementation uses stabdard java Logger and should not be configured in runtime.");
    }

    @Override
    public String logLevelToString(int loglevel) {
        throw new UnsupportedOperationException("logLevelToString is not supported in this implementation.");
    }

    @Override
    public int stringToLogLevel(String loglevel) {
        throw new UnsupportedOperationException("stringToLogLevel is not supported in this implementation.");
    }

    private String formatArgs(final String message, Object... args) {
        //this is a bit tricky : if args is null, it is passed to formatting
        //(and yes this can still break depending on conversion of the formatter, see String.format)
        //else if there is no args, we return the message as-is, otherwise we pass args to formatting normally.
        if (args != null && args.length == 0) {
            return message;
        } else {
            return String.format(message, args);
        }
    }
}
