package ch5.step2;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */

public class UserService_Ex2 {
    UserDao userDao;

    public UserService_Ex2(UserDao userDao) {
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
        Level currentLevel = user.getLevel();
        if (currentLevel == Level.BASIC) user.setLevel(Level.SILVER);
        else if(currentLevel == Level.SILVER) user.setLevel(Level.GOLD);
        userDao.update(user);

    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel){
            case BASIC: return (user.getLogin() >= 50);
            case SILVER: return (user.getRecommend() >= 30);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level" + currentLevel);
        }
    }


    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
