package com.joshlong.lucene.graalvm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

@Slf4j
@TypeHint(types = { org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl.class, },
		access = { TypeAccess.DECLARED_CLASSES, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_FIELDS,
				TypeAccess.DECLARED_METHODS, })
public class LuceneNativeConfiguration implements NativeConfiguration {

	LuceneNativeConfiguration() {
		log.info("contributing Spring Native @TypeHints for Lucene indexing");
	}

}
