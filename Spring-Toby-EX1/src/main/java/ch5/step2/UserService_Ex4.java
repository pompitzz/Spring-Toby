package ch5.step2;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */

public class UserService_Ex4 {
    private UserDao userDao;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;


    public UserService_Ex4(UserDao userDao, UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userDao = userDao;
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void upgradeLevels() {
        userDao.getAll().forEach(user -> {
            if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                userLevelUpgradePolicy.upgradeLevel(user);
                userDao.update(user);
            }
        });
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
