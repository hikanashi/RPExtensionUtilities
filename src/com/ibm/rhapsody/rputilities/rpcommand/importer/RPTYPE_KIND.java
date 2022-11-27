package com.ibm.rhapsody.rputilities.rpcommand.importer;

public enum RPTYPE_KIND {
    ENUM("Enumeration"),
    LANG("Language"),
    STRUCT("Structure"),
    TYPEDEF("Typedef"),
    UNION("Union"),
    ;

    private final String kind_;

    private RPTYPE_KIND(final String kind) {
        kind_ = kind;
    }

    public String getString() {
        return kind_;
    }
}