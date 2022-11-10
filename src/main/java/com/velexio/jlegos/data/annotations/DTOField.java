package com.velexio.jlegos.data.annotations;

import com.velexio.jlegos.data.enums.DTOClassType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used To Annotate the fields in a POJO class to indicate to code generation tool
 * to include in creation of DTO class
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface DTOField {
    DTOClassType[] dtoClassTypes() default {DTOClassType.CREATE_DTO, DTOClassType.UPDATE_DTO};
}
