package kz.greetgo.sandbox.controller.errors;

public class InvalidClientData extends RestError {
    public InvalidClientData() {
        super(400, "Invalid client data!");
    }
}
