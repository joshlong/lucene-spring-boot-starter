package com.joshlong.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LuceneTemplate implements LuceneOperations {

	private final Analyzer analyzer;

	private final Object monitor = new Object();

	private final Directory indexDirectory;

	private final String defaultIndexField;

	private final AtomicReference<IndexReader> reader = new AtomicReference<>();

	private final AtomicReference<IndexSearcher> searcher = new AtomicReference<>();

	public LuceneTemplate(Analyzer analyzer, String defaultIndexField, Directory directory) {
		this.analyzer = analyzer;
		this.indexDirectory = directory;
		this.defaultIndexField = defaultIndexField;
	}

	private IndexReader buildIndexReader() {
		try {
			return DirectoryReader.open(this.indexDirectory);
		} //
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private IndexWriter buildIndexWriter() {
		try {
			var iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			return new IndexWriter(this.indexDirectory, iwc);
		} //
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private IndexSearcher buildIndexSearcher(IndexReader reader) {
		return new IndexSearcher(reader);
	}

	@Override
	public <T> void write(Iterable<T> listOfItems, DocumentWriteMapper<T> mapper) {
		Assert.notNull(listOfItems, () -> "the collection should be non-null");
		this.write(iw -> {
			for (var item : listOfItems) {
				var write = mapper.map(item);
				iw.updateDocument(write.term(), write.document());
			}
		});
	}

	@Override
	public void write(IndexWriterCallback callback) {
		try (var iw = buildIndexWriter()) {
			callback.executeWithIndexWriter(iw);
		}
		catch (Exception exception) {
			ReflectionUtils.rethrowRuntimeException(exception);
		}
	}

	@Override
	public <T> List<T> search(Query query, int maxResults, DocumentSearchMapper<T> mapper) {
		try {
			var search = this.search(query, maxResults);
			var results = new ArrayList<T>();
			for (var sd : search.scoreDocs) {
				var doc = searcher.get().doc(sd.doc);
				var map = mapper.map(doc);
				results.add(map);
			}
			return results;
		} //
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public TopDocs search(Query q, int max) {
		try {
			if (this.reader.get() == null) {
				synchronized (this.monitor) {
					this.reader.set(buildIndexReader());
					this.searcher.set(buildIndexSearcher(this.reader.get()));
				}
			}
			var indexSearcher = this.searcher.get();

			return indexSearcher.search(q, max);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> List<T> search(String query, int max, DocumentSearchMapper<T> mapper) {
		try {
			return search(buildQueryParserFor(this.defaultIndexField, query), max, mapper);
		} //
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Query buildQueryParserFor(String field, String queryStr) throws Exception {
		var qp = new QueryParser(field, analyzer);
		return qp.parse(queryStr);
	}

}
