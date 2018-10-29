package org.medvedev.nikita.commands;

public class HandleError extends Exception {
    public HandleError(String msg)
    {
        super(msg);
    }
}
