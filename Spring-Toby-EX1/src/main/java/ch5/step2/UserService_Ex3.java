package ch5.step2;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */

public class UserService_Ex3 {
    private UserDao userDao;
    public static final int MIN_LONGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;


    public UserService_Ex3(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        userDao.getAll().forEach(user -> {
            if(canUpgradeLevel(user)){
                upgradeLevel(user);
            }
        });
    }

    private void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC: return (user.getLogin() >= MIN_LONGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level" + currentLevel);
        }
    }


    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
