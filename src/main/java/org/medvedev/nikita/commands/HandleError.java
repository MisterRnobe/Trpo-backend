package org.medvedev.nikita.commands;

class HandleError extends Exception {
    private final int code;
    HandleError(int code)
    {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
