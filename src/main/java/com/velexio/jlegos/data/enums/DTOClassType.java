package com.velexio.jlegos.data.enums;

/**
 * Used to annotate a POJO data class object to be used by auto code generator
 * to create a compatible DTO class
 */
public enum DTOClassType {

    CREATE_DTO(Names.CREATE_NAME),
    UPDATE_DTO(Names.UPDATE_NAME);

    private String name;

    DTOClassType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static class Names {
        public static final String CREATE_NAME = "CREATE_DTO";
        public static final String UPDATE_NAME = "UPDATE_DTO";
    }

    public String getSuffix() {
        switch (this) {
            case CREATE_DTO:
                return "CreateDTO";
            case UPDATE_DTO:
                return "UpdateDTO";
            default:
                return "";
        }
    }

}
