package com.velexio.jlegos.data.enums;

public enum DTOClassType {

    BASE_DTO(Names.BASE_NAME),
    CREATE_DTO(Names.CREATE_NAME),
    UPDATE_DTO(Names.UPDATE_NAME),
    DELETE_DTO(Names.DELETE_NAME);


    DTOClassType(String name) {
    }

    public static class Names {
        public static final String BASE_NAME = "BASE_DTO";
        public static final String CREATE_NAME = "CREATE_DTO";
        public static final String UPDATE_NAME = "UPDATE_DTO";
        public static final String DELETE_NAME = "DELETE_DTO";
    }

    public String getSuffix(DTOClassType classType) {
        switch (classType) {
            case CREATE_DTO:
                return "CreateDTO";
            case UPDATE_DTO:
                return "UpdateDTO";
            case DELETE_DTO:
                return "DeleteDTO";
            default:
                return "DTO";
        }
    }

}
