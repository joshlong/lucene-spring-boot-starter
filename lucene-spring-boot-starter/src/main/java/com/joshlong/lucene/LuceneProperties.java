package com.joshlong.lucene;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("lucene")
public class LuceneProperties {

	private Search search = new Search();

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Search {

		private Resource indexDirectoryResource;

	}

}
