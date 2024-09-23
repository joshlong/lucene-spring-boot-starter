package com.joshlong.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

public interface DocumentWriteMapper<T> {

	DocumentWrite map(T t) throws Exception;

	record DocumentWrite(Term term, Document document) {
	}

}
