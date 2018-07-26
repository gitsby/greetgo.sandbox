package kz.greetgo.sandbox.controller.errors;

public class NoCharmError extends RestError {
    public NoCharmError() {
        super(400, "No such charm!");
    }
}
