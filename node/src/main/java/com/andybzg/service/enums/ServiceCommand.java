package com.andybzg.service.enums;

public enum ServiceCommand {

    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public boolean equals(String cmd) {
        return this.toString().equals(cmd);
    }

    public static ServiceCommand fromValue(String str) {
        for (ServiceCommand command : ServiceCommand.values()) {
            if (command.value.equals(str)) {
                return command;
            }
        }
        return null;
    }
}
