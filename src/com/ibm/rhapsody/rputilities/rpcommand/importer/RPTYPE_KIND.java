package com.ibm.rhapsody.rputilities.rpcommand.importer;

public enum RPTYPE_KIND {
    ENUM("Enumeration"),
    LANG("Language"),
    STRUCT("Structure"),
    TYPEDEF("Typedef"),
    UNION("Union"),
    ;

    public static final String INPLICIT_MARK = "@";
    public static final String QUALIFIED_DELIMITER = "::";
    public static final String INPLICIT_PREFIX = "Implicit";

    private final String kind_;

    private RPTYPE_KIND(final String kind) {
        kind_ = kind;
    }

    public String getString() {
        return kind_;
    }

    public String getImplicitName(String name) {
        String implictname = name.replaceAll(INPLICIT_MARK, INPLICIT_PREFIX + getString());
        implictname = implictname.replaceAll(QUALIFIED_DELIMITER, "__");
        return implictname;
    }
}