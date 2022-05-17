package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class ItemDBStore {
    private static final Logger LOG = LoggerFactory.getLogger(ItemDBStore.class.getName());

    private final SessionFactory sf;

    public ItemDBStore(SessionFactory sf) {
        this.sf = sf;
    }

    public boolean create(Item item) {
        return tx(
                session -> {
                    session.persist(item);
                    LOG.info("{}:{} заявка сохранена", item.getId(), item.getName());
                    return true;
                }
        );
    }

    public Optional<Item> findById(int id) {
        return tx(
                session -> {
                    Optional<Item> result = Optional.ofNullable(session.get(Item.class, id));
                    LOG.info("Результат поиска по {}:{}", id, result.get().getName());
                    return result;
                }
        );
    }

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

    public List<Item> findAll(final User user) {
        return tx(
                session -> {
                    final Query query = session
                            .createQuery("from Item where user=:user")
                            .setParameter("user", user);
                    LOG.info("Найдено заявок {}", query.list().size());
                    return query.list();
                }
        );
    }

    public List<Item> findNew(final User user) {
        return tx(
                session -> {
                    final Query query = (Query) session
                            .createQuery("from Item where user=:user and done is null")
                            .setParameter("user", user).list();
                    LOG.info("Найдено новых заявок {}", query.list().size());
                    return query.list();
                }
        );
    }

    public List<Item> findCompleted() {
        return tx(
                session -> {
                    final Query query = session.createQuery("from Item  where done is not null");
                    LOG.info("Найдено завершенных заявок {}", query.list().size());
                    return query.list();
                }
        );
    }

    public List<Item> findDone(final User user) {
        return this.tx(
                session -> session
                        .createQuery("from Item where user=:user and done is not null")
                        .setParameter("user", user).list()
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