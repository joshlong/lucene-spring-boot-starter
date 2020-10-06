package com.joshlong.lucene;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import java.util.List;

public interface LuceneOperations {

	void write(IndexWriterCallback writer);

	<T> void write(Iterable<T> listOfItems, DocumentWriteMapper<T> mapper);

	TopDocs search(Query q, int max);

	<T> List<T> search(Query query, int maxRows, DocumentSearchMapper<T> mapper);

	<T> List<T> search(String query, int maxRows, DocumentSearchMapper<T> mapper);

}
