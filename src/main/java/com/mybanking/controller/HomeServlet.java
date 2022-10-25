package com.mybanking.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (!path.equals("/")) {
            req.getRequestDispatcher("/static/html/error.html").forward(req, resp);
            return;
        }

        if (req.getSession(false) != null) {
            req.getRequestDispatcher("/enter").forward(req, resp);
        }

        req.getRequestDispatcher("/static/html/homepage.html").forward(req, resp);
    }
}
