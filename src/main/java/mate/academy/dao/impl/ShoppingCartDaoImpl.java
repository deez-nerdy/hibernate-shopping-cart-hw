package mate.academy.dao.impl;

import java.util.Optional;
import mate.academy.dao.ShoppingCartDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class ShoppingCartDaoImpl implements ShoppingCartDao {
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();
            session.save(shoppingCart);
            transaction.commit();
            return shoppingCart;
        } catch (Exception e) {
            transactionNullCheck(transaction);
            throw new DataProcessingException("Could not save shopping cart: " + shoppingCart, e);
        } finally {
            sessionClose(session);
        }
    }

    @Override
    public Optional<ShoppingCart> getByUser(User user) {
        try (Session session = openSession()) {
            return session.createQuery("from ShoppingCart sc "
                            + "where sc.user.id = :userId", ShoppingCart.class)
                    .setParameter("userId", user.getId())
                    .uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException("Could not get shopping cart by user: " + user, e);
        }
    }

    @Override
    public void update(ShoppingCart shoppingCart) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();
            session.merge(shoppingCart);
            transaction.commit();
        } catch (Exception e) {
            transactionNullCheck(transaction);
            throw new DataProcessingException("Could not merge shopping cart: " + shoppingCart, e);
        } finally {
            sessionClose(session);
        }
    }

    private Session openSession() {
        return HibernateUtil.getSessionFactory().openSession();
    }

    private void transactionNullCheck(Transaction transaction) {
        if (transaction != null) {
            transaction.rollback();
        }
    }

    private void sessionClose(Session session) {
        if (session != null) {
            session.close();
        }
    }
}
