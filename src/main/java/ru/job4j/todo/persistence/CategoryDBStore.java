package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.Category;

import java.util.List;
import java.util.function.Function;

@Repository
public class CategoryDBStore {
    private final SessionFactory sf;

    public CategoryDBStore(SessionFactory sf) {
        this.sf = sf;
    }

    public List<Category> findAll() {
        return tx(session -> session.createQuery("from Category")
                .list()
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