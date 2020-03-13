package ch6.step1;

import ch5.step2.Level;
import ch5.step2.User;
import ch5.step2.UserDao;
import ch5.step2.UserLevelUpgradePolicy;
import lombok.Setter;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */

@Setter
public class UserService_Ex1 {
    private UserDao userDao;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;
    private PlatformTransactionManager transactionManager;
    private MailSender mailSender;

    public UserService_Ex1(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy, PlatformTransactionManager transactionManager, MailSender mailSender) {
        this.userDao = userDao;
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
        this.transactionManager = transactionManager;
        this.mailSender = mailSender;
    }

public void upgradeLevels() throws SQLException {
    TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
    try {
        upgradeLevelsInternal();
        transactionManager.commit(status);
    } catch (Exception e) {
        transactionManager.rollback(status);
        throw e;
    }
}

    private void upgradeLevelsInternal() {
        userDao.getAll().forEach(user -> {
            if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                userLevelUpgradePolicy.upgradeLevel(user);
                userDao.update(user);
                sendUpgradeEmail(user);
            }
        });
    }

    private void sendUpgradeEmail(User user) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(user.getEmail());
    mailMessage.setFrom("useradmin@gmail.com");
    mailMessage.setSubject("Upgrade 안내");
    mailMessage.setText("사용자 님의 등급이" + user.getLevel().name() + "로 업그레이드 되었습니다.");

    this.mailSender.send(mailMessage);
}

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
