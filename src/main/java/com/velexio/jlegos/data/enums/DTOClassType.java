package com.velexio.jlegos.data.enums;

/**
 * Used to annotate a POJO data class object to be used by auto code generator
 * to create a compatible DTO class
 */
public enum DTOClassType {

    CREATE_DTO(Names.CREATE_NAME),
    UPDATE_DTO(Names.UPDATE_NAME);

    private String name;

    /**
     * Constructor to set the name
     *
     * @param name
     */
    DTOClassType(String name) {
        this.name = name;
    }

    /**
     * Function to get name
     *
     * @return String name value
     */
    public String getName() {
        return name;
    }

    /**
     * the suffice generation
     *
     * @return String
     */
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

    /**
     * Static class to set names
     */
    public static class Names {
        public static final String CREATE_NAME = "CREATE_DTO";
        public static final String UPDATE_NAME = "UPDATE_DTO";
    }

}
