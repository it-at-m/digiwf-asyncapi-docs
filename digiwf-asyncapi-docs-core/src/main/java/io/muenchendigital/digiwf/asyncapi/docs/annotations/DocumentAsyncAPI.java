package io.muenchendigital.digiwf.asyncapi.docs.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Automatically document spring cloud stream producer and consumer functions that reside in a spring component.
 * Therefore, annotate the class with @DocumentAsyncAPI(payload = Payload.class).
 *
 * Restrictions: This annotation only works for regular producer functions. If you use dynamic routing,
 * you have to specify producers manually.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentAsyncAPI {
    Class<?> payload();
    boolean functionRouter() default false;
    String typeHeader() default "";
}
