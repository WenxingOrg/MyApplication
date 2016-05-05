package com.example.wzhao1.myapplication.util.log;

import android.util.Log;

/**
 * A more natural android logging facility.
 *
 * WARNING: CHECK OUT COMMON PITFALLS BELOW
 *
 * Unlike {@link android.util.Log}, Log provides sensible defaults.
 * Debug and Verbose logging is enabled for applications that
 * have "android:debuggable=true" in their AndroidManifest.xml.
 * For apps built using SDK Tools r8 or later, this means any debug
 * build.  Release builds built with r8 or later will have verbose
 * and debug log messages turned off.
 *
 * The default tag is automatically set to your app's packagename,
 * and the current context (eg. activity, service, application, etc)
 * is appended as well.  You can add an additional parameter to the
 * tag using {@link #Log(String)}.
 *
 * Log-levels can be programatically overridden for specific instances
 * using {@link #Log(String, boolean, boolean)}.
 *
 * Log messages may optionally use {@link String#format(String, Object...)}
 * formatting, which will not be evaluated unless the log statement is output.
 * Additional parameters to the logging statement are treated as varrgs parameters
 * to {@link String#format(String, Object...)}
 *
 * Also, the current file and line is automatically appended to the tag
 * (this is only done if debug is enabled for performance reasons).
 *
 * COMMON PITFALLS:
 * * Make sure you put the exception FIRST in the call.  A common
 *   mistake is to place it last as is the android.util.Log convention,
 *   but then it will get treated as varargs parameter.
 * * vararg parameters are not appended to the log message!  You must
 *   insert them into the log message using %s or another similar
 *   format parameter
 *
 * Usage Examples:
 *
 * LogMdx.v("hello there");
 * LogMdx.d("%s %s", "hello", "there");
 * LogMdx.e( exception, "Error during some operation");
 * LogMdx.w( exception, "Error during %s operation", "some other");
 *
 *
 */
public final class DLog {

    /** The printer that will be used to write the log lines.*/
    private static final LogPrinter PRINTER;

    static {
        if (isAndroidEnvironment()) {
            PRINTER = new AndroidPrinter();
        } else {
            PRINTER = new DefaulPrinter();
        }
    }

    /** Private constructor to avoid any DLog instance.*/
    private DLog() {
        //Empty constructor.
    }

    /** Verifies if the Android Log is on the classpath and if is not Stub.
     * @return true if android environment is detected.
     */
    private static boolean isAndroidEnvironment() {
        try {
            Class.forName("android.util.Log");
            Log.d("DLog", "Android detected");
            return true;
        } catch (ClassNotFoundException e) {
            // do nothing not android env.
        } catch (RuntimeException e) {
            // do nothing Log is present but is an stub.
        }
        return false;
    }

    /** Send a VERBOSE log message.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int v(String message, Object... args) {
        return PRINTER.v(message, args);
    }

    /** Send a VERBOSE log message and log the exception.
     * @param throwable the exception to log.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int v(Throwable throwable, String message, Object... args) {
        return PRINTER.v(throwable, message, args);
    }

    /** Send a DEBUG log message.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int d(String message, Object... args) {
        return PRINTER.d(message, args);
    }

    /** Send a DEBUG log message and log the exception.
     * @param throwable the exception to log.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int d(Throwable throwable, String message, Object... args) {
        return PRINTER.d(throwable, message, args);
    }

    /** Send a INFO log message.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int i(String message, Object... args) {
        return PRINTER.i(message, args);
    }

    /** Send a INFO log message and log the exception.
     * @param throwable the exception to log.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int i(Throwable throwable, String message, Object... args) {
        return PRINTER.i(throwable, message, args);
    }

    /** Send a WARNING log message.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int w(String message, Object... args) {
        return PRINTER.w(message, args);
    }

    /** Send a WARNING log message and log the exception.
     * @param throwable the exception to log.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int w(Throwable throwable, String message, Object... args) {
        return PRINTER.w(throwable, message, args);
    }

    /** Send a ERROR log message.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int e(String message, Object... args) {
        return PRINTER.e(message, args);
    }

    /** Send a ERROR log message and log the exception.
     * @param throwable the exception to log.
     * @param message the message to print.
     * @param args the optional args to format the message.
     * @return The number of bytes written.
     */
    public static int e(Throwable throwable, String message, Object... args) {
        return PRINTER.e(throwable, message, args);
    }

    /** Tells if DLog is in debug level.
     * @return true if debug level is enabled.
     */
    public static boolean isDebugEnabled() {
        return PRINTER.isDebugEnabled();
    }

    /** Tells if DLog is in verbose level.
     * @return true if verbose level is enabled.
     */
    public static boolean isVerboseEnabled() {
        return PRINTER.isVerboseEnabled();
    }

    /** The current logging level.
     * @return the number representing the log level.
     */
    public static int getLoggingLevel() {
        return PRINTER.getLoggingLevel();
    }

    /** Sets the logging level.
     * @param level the log level.
     */
    public static void setLoggingLevel(int level) {
        PRINTER.setLoggingLevel(level);
    }

    /** Transforms a log level number to an String representation.
     * @param loglevel the loglevel.
     * @return the name of the log level.
     */
    public static String logLevelToString(int loglevel) {
        return PRINTER.logLevelToString(loglevel);
    }

    /** Translates an String log level.
     * @param loglevel the string log level.
     * @return the corresponding integer value.
     */
    public static int stringToLogLevel(String loglevel) {
        return PRINTER.stringToLogLevel(loglevel);
    }

}