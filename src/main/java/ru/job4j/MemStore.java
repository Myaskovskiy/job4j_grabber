package ru.job4j;

import java.util.ArrayList;
import java.util.List;

public class MemStore implements Store {
    private List<Post> list = new ArrayList<>();
    private int id = 0;

    @Override
    public void save(Post post) {
        post.setId(id++);
        this.list.add(post);
    }

    @Override
    public List<Post> getAll() {
        return this.list;
    }

    @Override
    public Post findById(int id) {
        int index = indexOf(id);
        return index != -1 ? list.get(index) : null;
    }

    private int indexOf(int id) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                index = i;
                break;
            }
        }
        return index;
    }
}
