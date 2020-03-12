package ch5.step2;

import java.util.List;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/11
 */

public class UserService_Ex1 {
    UserDao userDao;

    public UserService_Ex1(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        users.forEach(user -> {
            Level level = user.getLevel();
            boolean changed = false;
            if (level == Level.BASIC && user.getLogin() >= 50){
                user.setLevel(Level.SILVER);
                changed = true;
            }
            else if(level == Level.SILVER &&user.getRecommend() >= 30){
                user.setLevel(Level.GOLD);
                changed = true;
            }

            // 변경되었다면 업데이트를 호출해준다.
            if (changed) userDao.update(user);
        });
    }

    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }
}
