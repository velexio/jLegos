package com.velexio.jlegos.data.enums;

public enum DTOFieldScope {

    BASE_DTO(Names.BASE_NAME),
    CREATE_DTO(Names.CREATE_NAME),
    UPDATE_DTO(Names.UPDATE_NAME),
    DELETE_DTO(Names.DELETE_NAME);


    DTOFieldScope(String name) {
    }

    public static class Names {
        public static final String BASE_NAME = "BASE_DTO";
        public static final String CREATE_NAME = "CREATE_DTO";
        public static final String UPDATE_NAME = "UPDATE_DTO";
        public static final String DELETE_NAME = "DELETE_DTO";
    }

}
