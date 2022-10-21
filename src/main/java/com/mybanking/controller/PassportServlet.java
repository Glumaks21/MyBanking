package com.mybanking.controller;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.dao.ClientSqlDao;
import com.mybanking.data.dao.Dao;
import com.mybanking.data.entity.Client;
import com.mybanking.data.entity.Passport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;

@WebServlet("/registration")
public class PassportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.getRequestDispatcher("/static/html/registration.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        Dao<Client> dao = new ClientSqlDao(DataSourceHolder.getDataSource());

        Passport passport = new Passport().
            setNumber(req.getParameter("number")).
            setName(req.getParameter("name")).
            setSurname(req.getParameter("surname")).
            setPatronymic(req.getParameter("patronymic")).
            setSex(req.getParameter("sex")).
            setBirthday(Date.valueOf(req.getParameter("birthday")));
        Client client = new Client().
            setPhone(req.getParameter("phone")).
            setPassport(passport);

        dao.save(client);
        resp.sendRedirect("/registration");
    }
}

