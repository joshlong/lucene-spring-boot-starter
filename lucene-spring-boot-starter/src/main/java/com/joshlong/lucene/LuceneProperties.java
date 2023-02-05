package com.joshlong.lucene;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("lucene")
public record LuceneProperties(Search search) {

	public record Search(String defaultIndexField, Resource indexDirectoryResource) {
	}
}