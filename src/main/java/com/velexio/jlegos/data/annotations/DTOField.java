package com.velexio.jlegos.data.annotations;

import com.velexio.jlegos.data.enums.DTOFieldScope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
public @interface DTOField {
    DTOFieldScope[] dtoTypes() default {DTOFieldScope.BASE_DTO};
}
