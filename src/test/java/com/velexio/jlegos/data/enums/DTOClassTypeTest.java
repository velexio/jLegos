package com.velexio.jlegos.data.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DTOClassTypeTest {

    @Test
    void getCreateSuffixWorks() {
        assertEquals("CreateDTO", DTOClassType.CREATE_DTO.getSuffix());
    }

    @Test
    void getUpdateSuffixWorks() {
        assertEquals("UpdateDTO", DTOClassType.UPDATE_DTO.getSuffix());
    }


}