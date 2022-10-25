package com.mybanking.controller;

import com.mybanking.controller.security.HashHelper;
import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.dao.app.AppAccountSqlDao;
import com.mybanking.data.dao.app.PasswordHashesSqlDao;
import com.mybanking.data.entity.app.Account;
import com.mybanking.data.entity.app.PasswordHash;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppAccountSqlDao accountDao = new AppAccountSqlDao(DataSourceHolder.getDataSource());
        PasswordHashesSqlDao passwordDao = new PasswordHashesSqlDao(DataSourceHolder.getDataSource());

        String phone = req.getParameter("phone");
        String password = req.getParameter("password");

        Account account = accountDao.findByPhone(phone).get();
        PasswordHash passwordHash = passwordDao.find(account.getId()).get();

        String genHash = HashHelper.getSHA512SecurePassword(password, "salt");
        if (passwordHash.getHash().equals(genHash)) {
            req.getSession();
        }

        resp.sendRedirect("/");
    }
}
