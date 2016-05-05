package com.example.wzhao1.myapplication.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

/** Android Log Printer, this printer is only used if an Android environment
 * is detected.
 * @author gerardo.bercovich
 */
public final class AndroidPrinter implements LogPrinter {
    /** skip 6 stackframes to find the location where this was called. */
    private static final int STACK_SKIP_DEPTH = 6;

    /** The minimum log level to print.*/
    private int minimumLogLevel = Log.VERBOSE;
    /** The default tag to print logs.*/
    private static String tag = "";
    /** The bi-map containing the String-int log level mappings.*/
    private static final BiMap<Integer, String> LEVELS;

    static {
        HashBiMap<Integer, String> levelMap = HashBiMap.create();
        levelMap.put(Log.ASSERT, "ASSERT");
        levelMap.put(Log.DEBUG, "DEBUG");
        levelMap.put(Log.ERROR, "ERROR");
        levelMap.put(Log.INFO, "INFO");
        levelMap.put(Log.VERBOSE, "VERBOSE");
        levelMap.put(Log.WARN, "WARN");
        LEVELS = ImmutableBiMap.copyOf(levelMap);
    }

    /** Printer constructor.*/
    AndroidPrinter() {
        // do nothing.
    }

    /** Initialize Android printer, calling this method is optional but will
     *  improve the level of detail in debug mode.
     *  TODO evaluate other alternatives like dynamic or static injection.
     * @param context the application object to initialize the
     *  android printer, cannot be null.
     */
    public static void init(final Application context) {
        Preconditions.checkNotNull(context, "Context cannot be null");
        int logLevel;
        try {
            String packageName = context.getPackageName();
            final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
            logLevel = (flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0 ? Log.INFO : Log.VERBOSE;
            tag = packageName.toUpperCase(Locale.US);

            DLog.d("Configuring Logging, minimum log level is %s", DLog.logLevelToString(logLevel));
        } catch (NameNotFoundException e) {
            Log.e(tag, "Error configuring logger, default level will be used", e);
            logLevel = Log.INFO;
        }
        DLog.setLoggingLevel(logLevel);
    }

    @Override
    public int v(String message, Object... args) {
        if (getLoggingLevel() > Log.VERBOSE) {
            return 0;
        }
        return println(Log.VERBOSE, formatArgs(message, args));
    }

    @Override
    public int v(Throwable throwable, String message, Object... args) {
        if (getLoggingLevel() > Log.VERBOSE) {
            return 0;
        }
        return println(Log.VERBOSE, formatArgs(message, args) + '\n' + getStackTraceString(throwable));
    }

    @Override
    public int d(String message, Object... args) {
        if (getLoggingLevel() > Log.DEBUG) {
            return 0;
        }

        return println(Log.DEBUG, formatArgs(message, args));
    }

    @Override
    public int d(Throwable throwable, String message, Object... args) {
        if (getLoggingLevel() > Log.DEBUG) {
            return 0;
        }

        return println(Log.DEBUG, formatArgs(message, args) + '\n' + getStackTraceString(throwable));
    }

    @Override
    public int i(Throwable throwable, String message, Object... args) {
        if (getLoggingLevel() > Log.INFO) {
            return 0;
        }

        return println(Log.INFO, formatArgs(message, args) + '\n' + getStackTraceString(throwable));
    }

    @Override
    public int i(String message, Object... args) {
        if (getLoggingLevel() > Log.INFO) {
            return 0;
        }

        return println(Log.INFO, formatArgs(message, args));
    }

    @Override
    public int w(Throwable throwable, String message, Object... args) {
        if (getLoggingLevel() > Log.WARN) {
            return 0;
        }
        return println(Log.WARN, formatArgs(message, args) + '\n' + getStackTraceString(throwable));
    }

    @Override
    public int w(String message, Object... args) {
        if (getLoggingLevel() > Log.WARN) {
            return 0;
        }

        return println(Log.WARN, formatArgs(message, args));
    }

    @Override
    public int e(Throwable throwable, String message, Object... args) {
        if (getLoggingLevel() > Log.ERROR) {
            return 0;
        }

        return println(Log.ERROR, formatArgs(message, args) + '\n' + getStackTraceString(throwable));
    }

    @Override
    public int e(String message, Object... args) {
        if (getLoggingLevel() > Log.ERROR) {
            return 0;
        }

        return println(Log.ERROR, formatArgs(message, args));
    }

    @Override
    public boolean isDebugEnabled() {
        return getLoggingLevel() <= Log.DEBUG;
    }

    @Override
    public boolean isVerboseEnabled() {
        return getLoggingLevel() <= Log.VERBOSE;
    }

    @Override
    public String logLevelToString(int loglevel) {
        return LEVELS.get(loglevel);
    }

    @Override
    public int stringToLogLevel(String loglevel) {
        return LEVELS.inverse().get(loglevel);
    }

    @Override
    public int getLoggingLevel() {
        return minimumLogLevel;
    }

    @Override
    public void setLoggingLevel(int level) {
        minimumLogLevel = level;
    }

    private int println(int priority, String msg) {
        return Log.println(priority, getTag(), processMessage(msg));
    }

    private String processMessage(String msg) {
        String finalMsg = msg;
        if (getLoggingLevel() <= Log.DEBUG) {
            finalMsg = String.format("%s %s", Thread.currentThread().getName(), msg);
        }
        return finalMsg;
    }

    private String getTag() {
        String customTag = tag;
        if (getLoggingLevel() <= Log.DEBUG) {
            final StackTraceElement trace = Thread.currentThread().getStackTrace()[STACK_SKIP_DEPTH];
            customTag = String.format("%s/%s:%s", tag, trace.getFileName(), trace.getLineNumber());
        }
        return customTag;
    }

    //protected for testing.
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

    private static String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }

}
