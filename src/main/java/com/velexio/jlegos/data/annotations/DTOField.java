package com.velexio.jlegos.data.annotations;

import com.velexio.jlegos.data.enums.DTOClassType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface DTOField {
    DTOClassType[] dtoClassTypes() default {DTOClassType.BASE_DTO, DTOClassType.CREATE_DTO, DTOClassType.UPDATE_DTO};
}
