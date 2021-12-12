package com.joshlong.lucene.graalvm;

import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

@ResourceHint(patterns = { "org/commonmark/internal/util/entities.properties" })
@NativeHint(options = "-H:+AddAllCharsets")
@TypeHint(types = { org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl.class, },
		access = { TypeAccess.DECLARED_CLASSES, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_FIELDS,
				TypeAccess.DECLARED_METHODS, })
public class LuceneNativeConfiguration implements NativeConfiguration {

	LuceneNativeConfiguration() {
	}

}
