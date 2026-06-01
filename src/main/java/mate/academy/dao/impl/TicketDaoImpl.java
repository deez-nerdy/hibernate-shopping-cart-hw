package mate.academy.dao.impl;

import java.util.Optional;
import mate.academy.dao.TicketDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Ticket;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class TicketDaoImpl implements TicketDao {
    @Override
    public Ticket add(Ticket ticket) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = openSession();
            transaction = session.beginTransaction();
            session.persist(ticket);
            transaction.commit();
            return ticket;
        } catch (Exception e) {
            transactionNullCheck(transaction);
            throw new DataProcessingException("Could not persist ticket: " + ticket, e);
        } finally {
            sessionClose(session);
        }
    }

    @Override
    public Optional<Ticket> get(long id) {
        try (Session session = openSession()) {
            return Optional.ofNullable(session.get(Ticket.class, id));
        } catch (Exception e) {
            throw new DataProcessingException("Could not get ticket with id: " + id, e);
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
