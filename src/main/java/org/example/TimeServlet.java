package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;
    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getClass().getClassLoader().getResource("templates").getPath());
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=utf-8");

        String time = "";
        if (req.getParameterMap().containsKey("timezone")) {
            String zone = req.getParameter("timezone");
            time = ZonedDateTime.now(ZoneId.of(zone))
                    .format(DateTimeFormatter.ofPattern(
                            "'Date:' yyy-MM-dd, 'Time:' HH:mm:ss")) + ", UTC: " + ZonedDateTime.now(ZoneId.of(zone)).getOffset();
            resp.addCookie(new Cookie("lastTimezone", ZonedDateTime.now(ZoneId.of(zone)).getOffset().toString()));
        } else {
            try {
                Cookie[] cookies = req.getCookies();
                for (Cookie cookie : cookies) {
                    if(cookie.getName().equals("lastTimezone")) {
                        time = ZonedDateTime.now(ZoneId.of(cookie.getValue()))
                                .format(DateTimeFormatter.ofPattern(
                                        "'Date:' yyy-MM-dd, 'Time:' HH:mm:ss")) + ", UTC: " + ZonedDateTime.now(ZoneId.of(cookie.getValue())).getOffset();
                    }
                }
            } catch (NullPointerException npe) {
                time = ZonedDateTime.now(ZoneId.of("+0")).format(DateTimeFormatter.ofPattern(
                        "'Date:' yyy-MM-dd, 'Time:' HH:mm:ss")) + ", UTC: +0:00";
            }
        }

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", time)
        );

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();

    }
}
