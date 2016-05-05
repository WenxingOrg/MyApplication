package com.example.wzhao1.myapplication.util.log;

/** The Log printer interface.
 * @author gerardo.bercovich
 */
interface LogPrinter {

    /** Verbose log level.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int v(String message, Object... args);

    /** Debug log level.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int d(String message, Object... args);

    /** Info log level.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int i(String message, Object... args);

    /** Warning log level.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int w(String message, Object... args);

    /** Error log level.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int e(String message, Object... args);

    /** Verbose log level.
     * @param throwable a throwable to print the stacktrace on the log.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int v(Throwable throwable, String message, Object... args);

    /** Debug log level.
     * @param throwable a throwable to print the stacktrace on the log.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int d(Throwable throwable, String message, Object... args);

    /** Info log level.
     * @param throwable a throwable to print the stacktrace on the log.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int i(Throwable throwable, String message, Object... args);

    /** Warning log level.
     * @param throwable a throwable to print the stacktrace on the log.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int w(Throwable throwable, String message, Object... args);

    /** Error log level.
     * @param throwable a throwable to print the stacktrace on the log.
     * @param message the message to print on the log.
     * @param args the arguments to format the message.
     * @return The number of bytes written.
     */
    int e(Throwable throwable, String message, Object... args);

    /** Tells if if the debug is enable.
     * @return true if the debug is enable.
     */
    boolean isDebugEnabled();

    /** Tells if the verbose mode is enable.
     * @return true if verbose is enabled.
     */
    boolean isVerboseEnabled();

    /** Returns the current logging level.
     * @return the logging level.
     */
    int getLoggingLevel();

    /** Sets the logging level.
     * @param level the new level.
     */
    void setLoggingLevel(int level);

    /** Translates the int log level to a readable String representation.
     * @param loglevel the int log level.
     * @return the String human readable representation of the given log level.
     */
    String logLevelToString(int loglevel);

    /** Translates the log level name to the int value.
     * @param loglevel the log level name.
     * @return the int log level value for the given name.
     */
    int stringToLogLevel(String loglevel);
}
