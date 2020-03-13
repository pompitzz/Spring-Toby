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

public interface UserService {
    void add(User user);
    void upgradeLevels();
}