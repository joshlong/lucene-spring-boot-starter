package com.joshlong.lucene;

import org.apache.lucene.document.Document;

public interface DocumentSearchMapper<T> {

	T map(Document document) throws Exception;

}
