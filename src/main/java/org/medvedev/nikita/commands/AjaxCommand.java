package org.medvedev.nikita.commands;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AjaxCommand {
    private final String[] requiredFields;

    AjaxCommand(String[] requiredFields)
    {
        this.requiredFields = requiredFields;
    }

    protected abstract Object doCommand(Map<String, String> parameters) throws HandleError, SQLException;
    public final String execute(Map<?,?> parameters)
    {
        JSONObject response = new JSONObject();
        if (parameters.isEmpty())
        {
            response.put("status", "error");
            response.put("message", "Empty request!");
            return JSON.toJSONString(response);
        }

        boolean missing = false;
        for (String s: requiredFields) {
            if (parameters.get(s) == null) {
                missing = true;
                break;
            }
        }

        if (missing)
        {
            response.put("status", "ok");
            response.put("message", "Wrong parameters!");
        }
        else {
            try {
                Object o = doCommand(convertToStringMap(parameters));
                response.put("status", "ok");
                response.put("body", o);
            }
            catch (ClassCastException exception)
            {
                response.put("status", "error");
                response.put("message", "Wrong type of a parameter!");
            }
            catch (HandleError e)
            {
                response.put("status", "error");
                response.put("message", e.getMessage());
            }
            catch (SQLException sqlException)
            {
                response.put("status", "error");
                response.put("message", sqlException.getMessage());
            }

        }
        return JSON.toJSONString(response);
    }
    private Map<String, String> convertToStringMap(Map<?,?> map)
    {
        return map.entrySet().stream().map(e-> new AbstractMap.SimpleEntry<>((String) e.getKey(), ((String[]) e.getValue())[0]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }
}
