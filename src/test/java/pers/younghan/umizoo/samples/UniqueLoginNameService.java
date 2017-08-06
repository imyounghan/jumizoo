/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.04.
 */
public interface UniqueLoginNameService {
    boolean validate(String loginName, String correlationId);
}
