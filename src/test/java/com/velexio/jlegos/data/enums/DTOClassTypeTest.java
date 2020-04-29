package com.velexio.jlegos.data.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DTOClassTypeTest {

    @Test
    void getBaseSuffixWorks() {
        assertEquals("DTO", DTOClassType.BASE_DTO.getSuffix());
    }

    @Test
    void getCreateSuffixWorks() {
        assertEquals("CreateDTO", DTOClassType.CREATE_DTO.getSuffix());
    }

    @Test
    void getUpdateSuffixWorks() {
        assertEquals("UpdateDTO", DTOClassType.UPDATE_DTO.getSuffix());
    }

    @Test
    void getDeleteSuffixWorks() {
        assertEquals("DeleteDTO", DTOClassType.DELETE_DTO.getSuffix());
    }

}