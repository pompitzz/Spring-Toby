package ch1;

import ch1.step1.UserDao;
import ch1.step4.ConnectionMaker;
import ch1.step5.DaoFactory;
import ch1.step9.CountingConnectionMaker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Dongmyeong Lee
 * @since 2020/02/28
 */
public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        deleteUsers();
        // fromStep1To3();
        // step4(new MysqlConnectionMaker());
        // step5("Step5");
        // step7("Hello World");
        // step8("Step 8 Test");
        // step9("Step 9 Test");
        // step11("Step 11 Test");
        step12("Step 12 Test");

    }

    private static void deleteUsers() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        final Connection c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/springtoby", "sunlee", "pass"
        );

        final PreparedStatement ps = c.prepareStatement("truncate users");
        ps.execute();
        ps.close();
        c.close();
    }

    public static void fromStep1To3(String name) throws SQLException, ClassNotFoundException {
        System.out.println("================ fromStep1To3 ===================");
        addAndSelect(new UserDao(), name);
    }

    public static void step4(ConnectionMaker connectionMaker, String name) throws SQLException, ClassNotFoundException {
        System.out.println("=================== fromStep4 ===================");
        addAndSelect(new ch1.step4.UserDao(connectionMaker), name);
    }

    public static void step5(String name) throws SQLException, ClassNotFoundException {
        System.out.println("=================== fromStep5 ===================");
        addAndSelect(new DaoFactory().userDao(), name);
    }

    public static void step7(String name) throws ClassNotFoundException, SQLException{
        System.out.println("=================== step 7 ===================");
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ch1.step7.DaoFactory.class);
        final ch1.step4.UserDao dao = context.getBean("userDao", ch1.step4.UserDao.class);
        addAndSelect(dao, name);

        final ch1.step4.UserDao dao2 = context.getBean("userDao", ch1.step4.UserDao.class);
        System.out.println("dao == dao2 = " + (dao == dao2));
    }

    public static void step8(String name) throws ClassNotFoundException, SQLException{
        System.out.println("=================== step 8 ===================");
        final ch1.step8.UserDao dao = new ch1.step8.UserDao();
        addAndSelect(dao, name);
    }

    public static void step9(String name) throws ClassNotFoundException, SQLException{
        System.out.println("=================== step 9 ===================");
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ch1.step9.DaoFactory.class);
        final ch1.step4.UserDao dao = context.getBean("userDao", ch1.step4.UserDao.class);
        addAndSelect(dao, name);

        final CountingConnectionMaker connectionMaker = context.getBean("connectionMaker", CountingConnectionMaker.class);
        System.out.println("connectionMaker.getCount() = " + connectionMaker.getCount());

    }

    public static void step11(String name) throws ClassNotFoundException, SQLException{
        System.out.println("=================== step 11 ===================");
        final ApplicationContext context =
                new GenericXmlApplicationContext("ch1/step11/applicationContext.xml");
        final ch1.step10.UserDao dao = context.getBean("userDao", ch1.step10.UserDao.class);

        addAndSelect(dao, name);
    }

    public static void step12(String name) throws ClassNotFoundException, SQLException{
        System.out.println("=================== step 12 ===================");
        final ApplicationContext context =
                new GenericXmlApplicationContext("ch1/step12/applicationContext.xml");
//        final AnnotationConfigApplicationContext context =
//                new AnnotationConfigApplicationContext(ch1.step12.DaoFactory.class);

        final ch1.step12.UserDao dao = context.getBean("userDao", ch1.step12.UserDao.class);

        addAndSelect(dao, name);
    }

    private static void addAndSelect(UserDaoInterface userDao, String name) throws ClassNotFoundException, SQLException {
        final User user = new User();
        user.setId("sunlee22");
        user.setName(name);
        user.setPassword("pass");

        userDao.add(user);
        System.out.println("등록 : " + user.getId());


        final User findUser = userDao.get(user.getId());
        System.out.println("findUser = " + findUser);
    }
}
