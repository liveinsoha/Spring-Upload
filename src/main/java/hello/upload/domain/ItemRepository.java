package hello.upload.domain;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemRepository {

    private static Map<Long, Item> store = new HashMap<>();
    private Long sequence = 0L;

    public Long save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item.getId();
    }

    public Item findById(Long id) {
        return store.get(id); //없을 경우 null반환
    }
}
