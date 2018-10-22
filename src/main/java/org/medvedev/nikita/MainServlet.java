package org.medvedev.nikita;

import com.alibaba.fastjson.JSON;
import org.medvedev.nikita.commands.AjaxCommand;
import org.medvedev.nikita.commands.NotFoundCommand;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MainServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        //writer.println("Parameters: " + JSON.toJSONString(req.getParameterMap()));
        //writer.println("Servlet path: "+req.getServletPath());

        String commandString = req.getServletPath().substring(1);
        String ajaxResponse = null;

        if (!commandString.isEmpty())
        {
            String className = "org.medvedev.nikita.commands."+commandString+"Command";
            Class commandClass;
            try {
                commandClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                commandClass = NotFoundCommand.class;

            }
            try {
                AjaxCommand ajaxCommand = (AjaxCommand)commandClass.newInstance();
                ajaxResponse = JSON.toJSONString(ajaxCommand.doCommand(req.getParameterMap()));
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        writer.println(ajaxResponse);
        writer.close();

    }
}
