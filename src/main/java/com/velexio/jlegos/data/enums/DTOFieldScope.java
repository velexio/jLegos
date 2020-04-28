package com.velexio.jlegos.data.enums;

public enum DTOFieldScope {
    BASE(Names.BASE_NAME),
    CREATE(Names.CREATE_NAME),
    UPDATE(Names.UPDATE_NAME),
    DELETE(Names.DELETE_NAME);

    DTOFieldScope(String name) {
    }

    public static class Names {
        public static final String BASE_NAME = "BASE";
        public static final String CREATE_NAME = "CREATE";
        public static final String UPDATE_NAME = "UPDATE";
        public static final String DELETE_NAME = "DELETE";
    }

}
