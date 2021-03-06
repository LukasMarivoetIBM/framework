/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2019.
 */
package dev.galasa;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Provide a CPS property to set for a test variation
 * 
 * @author Michael Baylis
 *
 */
@Retention(RUNTIME)
public @interface TestVariationProperty {

    String property();

    String value();
}
