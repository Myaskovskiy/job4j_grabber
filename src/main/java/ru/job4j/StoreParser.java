package ru.job4j;

import java.util.ArrayList;
import java.util.List;

public class StoreParser implements Store {
    private List<Post> list = new ArrayList<>();

    @Override
    public void save(Post post) {
        this.list.add(post);
    }

    @Override
    public List<Post> getAll() {
        return this.list;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        Post res = null;
        for (int i = 0; i < this.list.size(); i++) {
            post = this.list.get(i);
            if (post.getId() == id) {
                res = post;
                break;
            }
        }
        return res;
    }
}
