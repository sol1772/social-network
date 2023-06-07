package com.getjavajob.training.maksyutovs.socialnetwork.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionManager {

    private static final Logger LOGGER = Logger.getLogger(TransactionManager.class.getName());
    private DataSourceHolder dataSourceHolder;

    public TransactionManager() {
        this.dataSourceHolder = DataSourceHolder.getInstance(null);
    }

    public TransactionManager(DataSourceHolder dataSourceHolder) {
        this.dataSourceHolder = dataSourceHolder;
    }

    public DataSourceHolder getDataSourceHolder() {
        return dataSourceHolder;
    }

    public void setDataSourceHolder(DataSourceHolder dataSourceHolder) {
        this.dataSourceHolder = dataSourceHolder;
    }

    public <T> T executeAction(Supplier<T> action) {
        try (Connection ignored = dataSourceHolder.getConnection()) {
            return action.get();
        } catch (SQLException e) {
            throw new DaoRuntimeException(e.getMessage(), e);
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

    public <T> T executeTransaction(Supplier<T> transaction) {
        try (Connection connection = dataSourceHolder.getConnection()) {
            boolean initialAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                T object = transaction.get();
                connection.commit();
                return object;
            } catch (DaoRuntimeException e) {
                rollbackTransaction(connection);
                throw new DaoRuntimeException(e.getMessage(), e);
            } finally {
                connection.setAutoCommit(initialAutoCommit);
            }
        } catch (SQLException e) {
            throw new DaoRuntimeException(e.getMessage(), e);
        } finally {
            dataSourceHolder.returnConnection();
        }
    }

    void rollbackTransaction(Connection con) {
        if (con != null) {
            try {
                con.rollback();
                LOGGER.log(Level.WARNING, "Transaction is being rolled back");
            } catch (SQLException e) {
                throw new DaoRuntimeException(e.getMessage(), e);
            }
        }
    }

}
