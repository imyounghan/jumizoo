/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.composition;

import java.lang.annotation.*;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LifecycleAnnotation {
    Lifecycle lifecycle() default Lifecycle.Singleton;
}
