package ru.job4j;

import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class TestGrabber {

    static Properties getProperties() {

        Properties properties = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("grabber.properties")) {
            properties.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return properties;
    }

    public static void main(String[] args) throws SQLException {

        Properties pr = getProperties();
        PsqlStore psqlStore = new PsqlStore(pr);
        LocalDateTime.parse("2022-09-28T12:06:56");
        Post post = new Post("Системный аналитик (Микросервисы)",
                 "https://career.habr.com/vacancies/1000111462",
                 "ЕМ ПРЕДСТОИТ ЗАНИМАТЬСЯ",
                  LocalDateTime.parse("2022-09-28T12:06:56")
        );
        psqlStore.save(post);
        List<Post> list = psqlStore.getAll();
        System.out.println(list.get(0));
        Post post1 = psqlStore.findById(1);
        System.out.println(post1);
    }
}
