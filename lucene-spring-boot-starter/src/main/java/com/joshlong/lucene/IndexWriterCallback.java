package com.joshlong.lucene;

import org.apache.lucene.index.IndexWriter;

public interface IndexWriterCallback {

	void executeWithIndexWriter(IndexWriter iw) throws Exception;

}
