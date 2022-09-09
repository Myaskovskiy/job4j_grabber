package ru.job4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Element description = document.select(".style-ugc").first();
        return description.text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> listPost = new ArrayList<>();
        for (int i = 1; i <= 1; i++) {
            Connection connection = Jsoup.connect(link + i);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            for (Element row : rows) {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String linkVacancy = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                Element titleElementDate = row.select(".vacancy-card__date").first();
                Element linkElementDate = titleElementDate.child(0);
                String date = String.format(linkElementDate.attr("datetime"));
                try {
                    String descr = retrieveDescription(linkVacancy);
                    Post post = new Post(vacancyName, linkVacancy, descr, this.dateTimeParser.parse(date));
                    listPost.add(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return listPost;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(habrCareerDateTimeParser);
        String link = PAGE_LINK;
        List<Post> list = habrCareerParse.list(link);
        System.out.println(list.get(0));
        System.out.println(list.size());
    }
}
