package com.mybanking.controller;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.dao.app.PasswordHashesSqlDao;
import com.mybanking.data.dao.app.AppAccountSqlDao;
import com.mybanking.data.dao.client.ClientSqlDao;
import com.mybanking.data.entity.Client;
import com.mybanking.data.entity.app.Account;
import com.mybanking.data.entity.app.PasswordHash;
import com.mybanking.controller.security.HashHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.getRequestDispatcher("/static/html/registration.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        String phone = req.getParameter("phone");
        if (checkRegistered(phone)) {
            try {
                req.getRequestDispatcher("/static/html/error.html").forward(req, resp);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        } else if (!checkClient(phone)) {
            try {
                req.getRequestDispatcher("/static/html/error.html").forward(req, resp);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        } else {
            registerNewAccount(req.getParameter("phone"), req.getParameter("password"));
            resp.sendRedirect("/");
        }
    }

    private boolean checkRegistered(String phone) {
        AppAccountSqlDao accountDao = new AppAccountSqlDao(DataSourceHolder.getDataSource());
        Optional<Account> optionalAccount = accountDao.findByPhone(phone);
        return optionalAccount.isPresent();
    }

    private boolean checkClient(String phone) {
        ClientSqlDao clientDao = new ClientSqlDao(DataSourceHolder.getDataSource());
        Optional<Client> optionalClient = clientDao.findByPhone(phone);
        return optionalClient.isPresent();
    }

    private void registerNewAccount(String phone, String password) {
        ClientSqlDao clientDao = new ClientSqlDao(DataSourceHolder.getDataSource());
        AppAccountSqlDao accountDao = new AppAccountSqlDao(DataSourceHolder.getDataSource());
        PasswordHashesSqlDao passwordHashDao = new PasswordHashesSqlDao(DataSourceHolder.getDataSource());

        Client dbClient = clientDao.findByPhone(phone).get();

        Account newAccount = new Account().
                setClient(dbClient);
        accountDao.save(newAccount);

        newAccount = accountDao.findByPhone(dbClient.getPhone()).get();
        String genHash = HashHelper.getSHA512SecurePassword(password, "salt");
        passwordHashDao.save(new PasswordHash().
                setAccountId(newAccount.getId()).
                setHash(genHash));
    }
}

