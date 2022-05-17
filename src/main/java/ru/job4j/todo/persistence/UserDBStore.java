package ru.job4j.todo.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.job4j.todo.model.User;

import java.util.Optional;
import java.util.function.Function;

@Repository
public class UserDBStore {
    private final SessionFactory sf;

    public UserDBStore(final SessionFactory sf) {
        this.sf = sf;
    }

    public boolean create(final User user) {
        boolean result = false;
        try {
            tx(session -> session.save(user));
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Optional<User> findById(int id) {
        return tx(session ->
                Optional.ofNullable(session.get(User.class, id))
        );
    }

     public boolean update(int id, User user) {
        boolean result = false;
        try {
            result = tx(session -> session.createQuery("update User set name=:name, password=:password "
                    + "where id=:id")
                    .setParameter("name", user.getName())
                    .setParameter("password", user.getPassword())
                    .setParameter("id", id)
                    .executeUpdate() > 0
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Optional<User> findByNamePassword(final String name, final String password) {
        return tx(session -> session.createQuery("from User where name=:name and password=:password")
                .setParameter("name", name)
                .setParameter("password", password)
                .uniqueResultOptional());
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