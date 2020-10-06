package com.joshlong.lucene;

import lombok.extern.log4j.Log4j2;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Log4j2
class LuceneApplicationTest {

    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Test
    void search() throws Exception {
        var desktop = new File(System.getenv("HOME") + "/Desktop/lucene-index");
        System.setProperty("search.index-directory-resource", "file://" + desktop.getAbsolutePath());

        // [id, uid, title, date, episodePhotoUri, description, dateAndTime, dataAndTime, episodeUri]
        var podcasts = loadPodcasts();
        var context = SpringApplication.run(LuceneApplication.class);
        var luceneTemplate = context.getBean(LuceneTemplate.class);
        luceneTemplate.write(podcasts, map -> {
            var doc = new Document();
            doc.add(new StringField("id", map.get("id"), Field.Store.YES));
            doc.add(new StringField("uid", map.get("uid"), Field.Store.YES));
            doc.add(new TextField("title", map.get("title"), Field.Store.YES));
            doc.add(new TextField("description", html2text(map.get("description")), Field.Store.YES));
            return new DocumentWriteMapper.DocumentWrite(new Term("uid", doc.get("uid")), doc);
        });

        List<String> eddu = luceneTemplate.search("Eddu", 10, new DocumentSearchMapper<String>() {
            @Override
            public String map(Document document) throws Exception {
                document.forEach(System.out::println);
                return document.get("uid");
            }
        });

        eddu.forEach(uid -> System.out.println(uid));
    }

    private static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    private Collection<Map<String, String>> loadPodcasts() {
        var responseEntity = restTemplate
                .exchange(URI.create("http://bootifulpodcast.fm/podcasts.json"),
                        HttpMethod.GET, null, new ParameterizedTypeReference<Collection<Map<String, String>>>() {
                        });
        Assert.isTrue(responseEntity.getStatusCode().is2xxSuccessful(), () -> "the HTTP response should be 200x");
        return responseEntity.getBody();
    }
}