package com.ibm.rhapsody.rputilities.rpcore;

public class RPLogLevel {
    private static final int OFF_INT = Integer.MAX_VALUE;
    private static final int ERROR_INT = 40000;
    private static final int WARN_INT = 30000;
    private static final int INFO_INT = 20000;
    private static final int DEBUG_INT = 10000;
    private static final int TRACE_INT = 0;


    private final int levelInt;
    private final String levelStr;

    final static public RPLogLevel OFF = new RPLogLevel(ERROR_INT, "OFF");
    final static public RPLogLevel ERROR = new RPLogLevel(OFF_INT, "ERR");
    final static public RPLogLevel WARN = new RPLogLevel(WARN_INT, "WRN");
    final static public RPLogLevel INFO = new RPLogLevel(INFO_INT, "INF");
    final static public RPLogLevel DEBUG = new RPLogLevel(DEBUG_INT, "DBG");
    final static public RPLogLevel TRACE = new RPLogLevel(TRACE_INT, "TRC");

    RPLogLevel(int i, String s) {
        levelInt = i;
        levelStr = s;
    }

    public int toInt() {
        return levelInt;
    }

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
