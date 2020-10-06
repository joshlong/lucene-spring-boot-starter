package com.joshlong.lucene;

import org.apache.lucene.index.IndexWriter;

interface IndexWriterCallback {

	void executeWithIndexWriter(IndexWriter iw) throws Exception;

}
