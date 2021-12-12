/*
 * Copyright Gunnar Morling
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.joshlong.lucene.graalvm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.AttributeImpl;

import java.lang.invoke.MethodHandle;

/**
 * An AttributeFactory creates instances of {@link AttributeImpl}s.
 *
 * @author Gunnar Morling
 */
@Slf4j
@TargetClass(className = "org.apache.lucene.util.AttributeFactory")
public final class AttributeFactorySubstitution {

	public AttributeFactorySubstitution() {
		log.info("contributing " + getClass().getName() + " graalvm substitution.");
	}

	@Substitute
	static final MethodHandle findAttributeImplCtor(Class<? extends AttributeImpl> clazz) {
		return null;
	}

}
