package ch5.step2;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */

public class UserService_Ex5 {
    private UserDao userDao;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;
    private DataSource dataSource;

    public UserService_Ex5(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy, DataSource dataSource) {
        this.userDao = userDao;
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
        this.dataSource = dataSource;
    }

    public void upgradeLevels() throws SQLException {
        TransactionSynchronizationManager.initSynchronization();
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        try {
            List<User> users = userDao.getAll();
            users.forEach(user -> {
                if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                    userLevelUpgradePolicy.upgradeLevel(user);
                    userDao.update(user);
                }
            });
        } catch (Exception e) {
            c.rollback();
        } finally {
            DataSourceUtils.releaseConnection(c, dataSource);
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }

    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
