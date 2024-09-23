package com.joshlong.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.store.FSDirectory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.StringUtils;

/**
 * Simplifies the configuration
 */

@Configuration
@ImportRuntimeHints(LuceneAutoConfiguration.LuceneHints.class)
@EnableConfigurationProperties(LuceneProperties.class)
class LuceneAutoConfiguration {

	@Bean
	@ConditionalOnProperty("lucene.search.index-directory-resource")
	@ConditionalOnMissingBean(LuceneTemplate.class)
	LuceneTemplate luceneTemplate(LuceneProperties properties, Analyzer analyzer) throws Exception {
		var defaultIndexField = properties.search().defaultIndexField();
		if (!StringUtils.hasText(defaultIndexField))
			defaultIndexField = "description";
		return new LuceneTemplate(analyzer, defaultIndexField,
				FSDirectory.open(properties.search().indexDirectoryResource().getFile().toPath()));
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

	static class LuceneHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
			hints.reflection().registerType(org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl.class,
					MemberCategory.values());
		}

	}

}
