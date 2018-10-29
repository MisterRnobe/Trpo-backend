package org.medvedev.nikita;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.BiFunction;

public class MainServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        executeFor(req, resp, (str, map) -> CommandManager.getInstance().doGet(str, map));

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //PrintWriter writer = resp.getWriter();
        //writer.println(JSON.toJSONString(req.getParameterMap()));
        //writer.close();
        executeFor(req, resp, (str, map) -> CommandManager.getInstance().doPost(str, map));
    }

    private void executeFor(HttpServletRequest req, HttpServletResponse resp, BiFunction<String, Map, String> handler) throws IOException
    {
        PrintWriter w = resp.getWriter();
        String commandString = req.getServletPath().substring(1);
        String ajaxResponse = handler.apply(commandString, req.getParameterMap());
        resp.setContentType("application/json");
        w.println(ajaxResponse);
        w.close();
    }
}
