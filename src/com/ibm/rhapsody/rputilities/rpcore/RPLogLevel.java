package com.ibm.rhapsody.rputilities.rpcore;

public class RPLogLevel {
    /**
     * Turn off log output
     */
    private static final int OFF_INT = Integer.MAX_VALUE;
    /**
     * Error level log output
     */
    private static final int ERROR_INT = 40000;
    /**
     * Warning level log output
     */
    private static final int WARN_INT = 30000;
    /**
     * Infomation level log output
     */
    private static final int INFO_INT = 20000;
    /**
     * Debug level log output
     */
    private static final int DEBUG_INT = 10000;
    /**
     * Detail debug level log output
     */
    private static final int TRACE_INT = 0;

    /**
     *
     */
    public final static RPLogLevel OFF = new RPLogLevel(ERROR_INT, "OFF");
    public final static RPLogLevel ERROR = new RPLogLevel(OFF_INT, "ERR");
    public final static RPLogLevel WARN = new RPLogLevel(WARN_INT, "WRN");
    public final static RPLogLevel INFO = new RPLogLevel(INFO_INT, "INF");
    public final static RPLogLevel DEBUG = new RPLogLevel(DEBUG_INT, "DBG");
    public final static RPLogLevel TRACE = new RPLogLevel(TRACE_INT, "TRC");

    private final int levelInt;
    private final String levelStr;
    /**
     * @param i
     * @param s
     */
    RPLogLevel(int i, String s) {
        levelInt = i;
        levelStr = s;
    }

    /**
     * @return
     */
    public int toInt() {
        return levelInt;
    }

    /**
     * @param levelInt
     * @return
     */
    public static RPLogLevel intToLevel(int levelInt) {
        switch (levelInt) {
        case (TRACE_INT):
            return TRACE;
        case (DEBUG_INT):
            return DEBUG;
        case (INFO_INT):
            return INFO;
        case (WARN_INT):
            return WARN;
        case (ERROR_INT):
            return ERROR;
        case (OFF_INT):
            return OFF;
        default:
            throw new IllegalArgumentException("Level integer [" + levelInt + "] not recognized.");
        }
    }

    /**
     * Returns the string representation of this Level.
     */
    public String toString() {
        return levelStr;
    }

}
