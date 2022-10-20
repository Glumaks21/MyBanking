package com.mybanking.controller;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.dao.ClientDao;
import com.mybanking.data.dao.Dao;
import com.mybanking.data.dao.PassportDao;
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
        Dao<Client> dao = new ClientDao(DataSourceHolder.getDataSource());


        Client client = new Client();
        client.setPhone(req.getParameter("phone"));
        Passport passport = new Passport();
        passport.setNumber(req.getParameter("number"));
        passport.setName(req.getParameter("name"));
        passport.setSurname(req.getParameter("surname"));
        passport.setPatronymic(req.getParameter("patronymic"));
        passport.setSex(req.getParameter("sex"));
        passport.setBirthday(Date.valueOf(req.getParameter("birthday")));
        client.setPassport(passport);

        dao.save(client);
        resp.sendRedirect("/registration");
    }
}

