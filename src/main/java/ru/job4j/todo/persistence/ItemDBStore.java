package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class ItemDBStore implements Store<Item> {
    private static final Logger LOG = LoggerFactory.getLogger(ItemDBStore.class.getName());

    private final SessionFactory sf;

    public ItemDBStore(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    public Optional<Item> create(Item item) {
        return tx(
                session -> {
                    session.save(item);
                    Optional<Item> result = Optional.ofNullable(session.get(Item.class, item.getId()));
                    LOG.info("{}:{} заявка сохранена", item.getId(), item.getName());
                    return result;
                }
        );
    }

    @Override
    public Optional<Item> findById(int id) {
        return tx(
                session -> {
                    Optional<Item> result = Optional.ofNullable(session.get(Item.class, id));
                    LOG.info("Результат поиска по {}:{}", id, result.get().getName());
                    return result;
                }
        );
    }

    @Override
    public boolean update(int id, Item item) {
        return tx(
                session -> {
                    int result = session.createQuery("update Item set name=:name, description=:description, "
                            + "created=:created, done=:done where id=:id")
                            .setParameter("id", id)
                            .setParameter("name", item.getName())
                            .setParameter("description", item.getDescription())
                            .setParameter("created", item.getCreated())
                            .setParameter("done", item.getDone())
                            .executeUpdate();
                    LOG.info("Результат обновления заявки {}", result > 0);
                    return result > 0;
                }
        );
    }

    public boolean delete(int id) {
        return tx(
                session -> {
                    int result = session.createQuery("delete from Item where id = :id")
                            .setParameter("id", id)
                            .executeUpdate();
                    LOG.info("Результат удаления заявки {}", result > 0);
                    return result > 0;
                }
        );
    }

    @Override
    public List<Item> findAll() {
        return tx(
                session -> {
                    final Query query = session.createQuery("from Item");
                    LOG.info("Найдено заявок {}", query.list().size());
                    return query.list();
                }
        );
    }

    @Override
    public List<Item> findNew() {
        return tx(
                session -> {
                    final Query query = session.createQuery("from Item  where done is null");
                    LOG.info("Найдено новых заявок {}", query.list().size());
                    return query.list();
                }
        );
    }

    @Override
    public List<Item> findCompleted() {
        return tx(
                session -> {
                    final Query query = session.createQuery("from Item  where done is not null");
                    LOG.info("Найдено завершенных заявок {}", query.list().size());
                    return query.list();
                }
        );
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = sf.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }
}