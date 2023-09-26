package org.example;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String time;
        if (req.getParameterMap().containsKey("timezone")) {
            String zone = req.getParameter("timezone");
            time = ZonedDateTime.now(ZoneId.of(zone))
                    .format(DateTimeFormatter.ofPattern(
                    "'Date:' yyy-MM-dd, 'Time:' HH:mm:ss")) + ", UTC: " + ZonedDateTime.now(ZoneId.of(zone)).getOffset();
        } else {
            time = ZonedDateTime.now(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern(
                    "'Date:' yyy-MM-dd, 'Time:' HH:mm:ss")) + ", UTC: +0:00";
        }
        resp.setContentType("text/html; charset=utf-8");
        resp.getWriter().write(time);
        resp.getWriter().close();
    }
}
