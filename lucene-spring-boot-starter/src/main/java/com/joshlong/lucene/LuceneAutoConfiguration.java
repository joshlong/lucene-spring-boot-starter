package com.joshlong.lucene;

import lombok.extern.log4j.Log4j2;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.store.FSDirectory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Simplifies the configuration
 */
@Log4j2
@Configuration
@EnableConfigurationProperties(LuceneProperties.class)
class LuceneAutoConfiguration {

	@Bean
	@ConditionalOnProperty("lucene.search.index-directory-resource")
	@ConditionalOnMissingBean(LuceneTemplate.class)
	LuceneTemplate luceneTemplate(LuceneProperties properties, Analyzer analyzer) throws Exception {
		return new LuceneTemplate(analyzer, "description",
				FSDirectory.open(properties.getSearch().getIndexDirectoryResource().getFile().toPath()));
	}

	@Bean
	@ConditionalOnMissingBean(Analyzer.class)
	Analyzer analyzer() {
		return new Analyzer() {

			@Override
			protected TokenStreamComponents createComponents(String fieldName) {
				var tokenizer = new StandardTokenizer();
				tokenizer.setMaxTokenLength(StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH);
				var filters = new StopFilter(new ASCIIFoldingFilter(new LowerCaseFilter(tokenizer)),
						CharArraySet.EMPTY_SET);
				return new TokenStreamComponents(tokenizer, filters);
			}
		};
	}

}
