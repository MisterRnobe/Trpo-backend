package org.medvedev.nikita;


import com.alibaba.fastjson.JSON;
import org.medvedev.nikita.commands.AjaxCommand;
import org.medvedev.nikita.commands.LoginCommand;
import org.medvedev.nikita.commands.NotFoundCommand;
import org.medvedev.nikita.commands.RegisterCommand;

import java.util.Map;
import java.util.TreeMap;

public class CommandManager {

    public static final String REGISTER = "register";
    public static final String LOGIN = "login";
    private final AjaxCommand notFound = new NotFoundCommand();

    private static CommandManager instance;
    public static CommandManager getInstance()
    {
        if (instance == null)
            instance = new CommandManager();
        return instance;
    }
    private final Map<String, AjaxCommand> getHandlers;
    private final Map<String, AjaxCommand> postHandlers;

    private CommandManager(){
        getHandlers = new TreeMap<>();
        postHandlers = new TreeMap<>();
        fillGet();
        fillPost();
    }

    public String doGet(String method, Map parameters)
    {
        AjaxCommand command;
        if ((command = getHandlers.get(method)) == null)
            command = notFound;

        return command.execute(parameters);
    }
    public String doPost(String method, Map parameters)
    {
        AjaxCommand command;
        if ((command = postHandlers.get(method)) == null)
            command = notFound;

        return command.execute(parameters);
    }
    private void fillGet()
    {

    }
    private void fillPost()
    {
        postHandlers.put(REGISTER, new RegisterCommand());
        postHandlers.put(LOGIN, new LoginCommand());
    }
}
