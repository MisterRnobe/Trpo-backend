package org.medvedev.nikita;


import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;
import org.medvedev.nikita.commands.*;

import java.util.Map;
import java.util.TreeMap;

public class CommandManager {

    public static final String REGISTER = "register";
    public static final String LOGIN = "login";
    public static final String ADD_NOTE = "add_note";
    public static final String GET_NOTES = "get_notes";
    private final AjaxCommand notFound = new NotFoundCommand();
    private final Logger logger = Logger.getLogger(CommandManager.class);

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
        //logger.info("Method: "+method+". GetHandlers: "+JSON.toJSONString(getHandlers));
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
        getHandlers.put(GET_NOTES, new GetNotesCommand());
    }
    private void fillPost()
    {
        postHandlers.put(REGISTER, new RegisterCommand());
        postHandlers.put(LOGIN, new LoginCommand());
        postHandlers.put(ADD_NOTE, new AddNoteCommand());
    }
}
