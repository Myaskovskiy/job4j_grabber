package ru.job4j;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(
                cfg.getProperty("url"),
                cfg.getProperty("username"),
                cfg.getProperty("password")
        );
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement("insert into post(name, text, link, created) values (?, ?, ?, ?) on conflict do nothing",
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, java.sql.Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement statement =
                     cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(getPost(resultSet));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement statement =
                     cnn.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    post = getPost(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    public Post getPost(ResultSet resultSet) throws SQLException {
        Post post = new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("text"),
                resultSet.getString("link"),
                resultSet.getTimestamp("created").toLocalDateTime()
        );
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

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
