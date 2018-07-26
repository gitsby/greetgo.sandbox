package kz.greetgo.sandbox.controller.errors;

public class NoClient extends RestError{
    public NoClient() {
        super(400, "No such client");
    }
}
