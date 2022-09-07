package ru.job4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HabrCareerParse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    private String retrieveDescription(String link) throws IOException {
        String vacancyDescription = " ";
        ArrayList<String> vacancyDescriptionList = new ArrayList<>();
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".style-ugc");
        rows.forEach(row -> {
            Element titleElement = row;
            String vacancyDescr = titleElement.text();
            vacancyDescriptionList.add(vacancyDescr);
        });
        for (String str : vacancyDescriptionList) {
            String str1 = vacancyDescription + String.format("%s%n", str);
            vacancyDescription = str1;
        }
        return vacancyDescription;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse();
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        ArrayList<Post> listPost = new ArrayList<>();
        for (int i = 1; i <= 1; i++) {
            Connection connection = Jsoup.connect(PAGE_LINK + i);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                Element titleElementDate = row.select(".vacancy-card__date").first();
                Element linkElementDate = titleElementDate.child(0);
                String date = String.format(linkElementDate.attr("datetime"));
                try {
                    String descr = habrCareerParse.retrieveDescription(link);
                    Post post = new Post(vacancyName, link, descr, habrCareerDateTimeParser.parse(date));
                    listPost.add(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        System.out.println(listPost.get(0));
        System.out.println(listPost.size());
    }
}
