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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

@Log4j2
@Configuration
class LuceneAutoConfiguration {


    @Bean
    LuceneTemplate luceneTemplate(
            @Value("${search.index-directory-resource}") Resource indexDirectory, Analyzer analyzer) throws Exception {
        return new LuceneTemplate(analyzer, "description", FSDirectory.open(indexDirectory.getFile().toPath()));
    }

   /* private Collection<Map<String,String>> loadPodcasts() {
        var rt = new RestTemplateBuilder().build();
        var responseEntity = rt.exchange(URI.create("http://bootifulpodcast.fm/podcasts.json"), HttpMethod.GET, null,
                new ParameterizedTypeReference<Collection<Podcast>>() {
                });
        Assert.isTrue(responseEntity.getStatusCode().is2xxSuccessful(), () -> "the HTTP response should be 200x");
        return responseEntity.getBody();
    }*/
/*



	@Bean
	ApplicationListener<ApplicationReadyEvent> ready(LuceneTemplate template) {
		return event -> {
			log.info("starting...");
			Iterable<Podcast> all = loadPodcasts();
			template.write(all, podcast -> {
				var term = new Term("uid", podcast.getUid());
				var result = new Document();
				result.add(new StringField("id", Long.toString(podcast.getId()), Field.Store.YES));
				result.add(new StringField("uid", podcast.getUid(), Field.Store.YES));
				result.add(new TextField("title", podcast.getTitle(), Field.Store.YES));
				result.add(new TextField("description", html2text(podcast.getDescription()), Field.Store.YES));
				result.add(new LongPoint("time", podcast.getDate().getTime()));
				return new DocumentWriteMapper.DocumentWrite(term, result);
			});

			var podcastList = template.search("Spring", 1000,
					document -> Collections.singletonMap("uid", document.get("uid")));
			for (var uid : podcastList)
				log.info("uid:" + uid);

		};
	}

	private static String html2text(String html) {
		return Jsoup.parse(html).text();
	}

*/

    @Bean
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
