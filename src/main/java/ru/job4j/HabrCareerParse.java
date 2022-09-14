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
    private static final int COUNT_PAGE = 1;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        Document document = null;
        try {
            document = connection.get();
        } catch (IllegalArgumentException | IOException iae) {
            iae.printStackTrace();
        }
        Element description = document.select(".style-ugc").first();
        return description.text();
    }

    private Post post(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        Element linkElement = titleElement.child(0);
        String vacancyName = titleElement.text();
        String linkVacancy = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        Element titleElementDate = row.select(".vacancy-card__date").first();
        Element linkElementDate = titleElementDate.child(0);
        String date = String.format(linkElementDate.attr("datetime"));
        String descr = retrieveDescription(linkVacancy);
        Post post = new Post(vacancyName, linkVacancy, descr, this.dateTimeParser.parse(date));
        return post;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> listPost = new ArrayList<>();
        for (int i = 1; i <= COUNT_PAGE; i++) {
            Connection connection = Jsoup.connect(link + i);
            Document document = null;
            try {
                document = connection.get();
            } catch (IllegalArgumentException | IOException iae) {
                iae.printStackTrace();
            }
            Elements rows = document.select(".vacancy-card__inner");
            for (Element row : rows) {
                Post post = post(row);
                listPost.add(post);
            }
        }
        return listPost;
    }

    public static void main(String[] args) {
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(habrCareerDateTimeParser);
        String link = PAGE_LINK;
        List<Post> list = habrCareerParse.list(link);
        System.out.println(list.get(0));
        System.out.println(list.size());
    }
}
