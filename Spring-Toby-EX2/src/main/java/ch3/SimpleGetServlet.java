package ch3;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Dongmyeong Lee
 * @since 2020/03/20
 */
public class SimpleGetServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");

        resp.getWriter().println("<HTML><BODY>");
        resp.getWriter().println("Hello " + name);
        resp.getWriter().println("</HTML></BODY>");

    }
}
