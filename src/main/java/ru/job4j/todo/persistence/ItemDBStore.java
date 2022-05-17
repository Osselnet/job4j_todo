package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public class ItemDBStore implements Store<Item> {
    private static final Logger LOG = LoggerFactory.getLogger(ItemDBStore.class.getName());

    private final SessionFactory sf;

    public ItemDBStore(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Optional<Item> create(Item item) {
        Session session = sf.openSession();
        session.beginTransaction();
        session.save(item);
        Optional<Item> result = Optional.ofNullable(session.get(Item.class, item.getId()));
        session.getTransaction().commit();
        session.close();
        LOG.info("{}:{} заявка сохранена", item.getId(), item.getName());
        return result;
    }

    @Override
    public Optional<Item> findById(int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        Optional<Item> result = Optional.ofNullable(session.get(Item.class, id));
        session.getTransaction().commit();
        session.close();
        LOG.info("Результат поиска по {}:{}", id, result.get().getName());
        return result;
    }

    @Override
    public boolean update(int id, Item item) {
        Session session = sf.openSession();
        session.beginTransaction();
        int result = session.createQuery("update Item set name = :nameItem, "
                + "description = :descriptionItem, created = :createdItem, "
                + "done = :doneItem where id = :idItem")
                .setParameter("nameItem", item.getName())
                .setParameter("descriptionItem", item.getDescription())
                .setParameter("createdItem", item.getCreated())
                .setParameter("doneItem", item.getDone())
                .setParameter("idItem", id)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
        LOG.info("Результат обновления заявки {}", result > 0);
        return result > 0;
    }

    public boolean delete(int id) {
        Session session = sf.openSession();
        session.beginTransaction();
        int result = session.createQuery("delete from Item where id = :idItem")
                .setParameter("idItem", id)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
        LOG.info("Результат удаления заявки {}", result > 0);
        return result > 0;
    }

    @Override
    public List<Item> findAll() {
        Session session = sf.openSession();
        session.beginTransaction();
        List result = session.createQuery("from Item")
                .list();
        session.getTransaction().commit();
        session.close();
        LOG.info("Найдено заявок {}", result.size());
        return result;
    }

    @Override
    public List<Item> findNew() {
        Session session = sf.openSession();
        session.beginTransaction();
        List result = session.createQuery("from Item  where done is null")
                .list();
        session.getTransaction().commit();
        session.close();
        LOG.info("Найдено новых заявок {}", result.size());
        return result;
    }

    @Override
    public List<Item> findCompleted() {
        Session session = sf.openSession();
        session.beginTransaction();
        List result = session.createQuery("from Item  where done is not null")
                .list();
        session.getTransaction().commit();
        session.close();
        LOG.info("Найдено завершенных заявок {}", result.size());
        return result;
    }
}