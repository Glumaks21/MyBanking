package servlet;

import data.DataSourceHolder;
import data.dao.Dao;
import data.dao.PassportDao;
import data.entity.Passport;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/register-complete")
public class PassportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("static/html/register_page.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        Dao<Passport> dao = new PassportDao(DataSourceHolder.getDataSource());

        Passport passport = new Passport();
        passport.setNumber(req.getParameter("number"));
        passport.setName(req.getParameter("name"));
        passport.setSurname(req.getParameter("surname"));
        passport.setPatronymic(req.getParameter("patronymic"));
        passport.setSex(req.getParameter("sex"));
        passport.setBirthday(Date.valueOf(req.getParameter("birthday")));

        dao.save(passport);
        resp.sendRedirect("static/html/register_page.html");
    }
}

