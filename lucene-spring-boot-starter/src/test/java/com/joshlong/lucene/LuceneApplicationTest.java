package com.joshlong.lucene;

import lombok.extern.log4j.Log4j2;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

@Log4j2
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LuceneApplicationTest {

	private final RestTemplate restTemplate = new RestTemplateBuilder().build();

	private File luceneIndexFolder;

	private LuceneTemplate template;

	@BeforeAll
	void setup() {
		this.luceneIndexFolder = new File(System.getenv("HOME") + "/Desktop/lucene-index");
		System.setProperty("lucene.search.index-directory-resource", "file://" + luceneIndexFolder.getAbsolutePath());
		var context = SpringApplication.run(LuceneApplication.class);
		this.template = context.getBean(LuceneTemplate.class);
		this.template.write(loadPodcasts(), map -> {
			var doc = new Document();
			doc.add(new StringField("id", map.get("id"), Field.Store.YES));
			doc.add(new StringField("uid", map.get("uid"), Field.Store.YES));
			doc.add(new TextField("title", map.get("title"), Field.Store.YES));
			doc.add(new TextField("description", html2text(map.get("description")), Field.Store.YES));
			return new DocumentWriteMapper.DocumentWrite(new Term("uid", doc.get("uid")), doc);
		});
	}

	@AfterAll
	void after() {
		FileSystemUtils.deleteRecursively(this.luceneIndexFolder);
		Assertions.assertFalse(this.luceneIndexFolder.exists());
	}

	@Test
	void search() {
		var results = this.template.search("Eddu", 10, document -> document.get("uid"));
		Assertions.assertTrue(results.size() > 0, "there should be one or more UIDs in the results");
		Assertions.assertTrue(luceneIndexFolder.exists());
		log.info(results);
	}

	private Collection<Map<String, String>> loadPodcasts() {
		var responseEntity = restTemplate.exchange(URI.create("http://api.bootifulpodcast.fm/site/podcasts"),
				HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Map<String, String>>>() {
				});
		Assert.isTrue(responseEntity.getStatusCode().is2xxSuccessful(), () -> "the HTTP response should be 200x.");
		return responseEntity.getBody();
	}

	private String html2text(String html) {
		return Jsoup.parse(html).text();
	}

	@Configuration
	@EnableAutoConfiguration
	static class LuceneApplication {

	}

}
