package com.mybanking.controller;

import com.mybanking.data.DataSourceHolder;
import com.mybanking.data.dao.app.AccountSqlDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/enter")
public class EnterAccountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AccountSqlDao dao = new AccountSqlDao(DataSourceHolder.getDataSource());

        
    }
}
