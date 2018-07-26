package kz.greetgo.sandbox.controller.errors;

public class InvalidParams extends RestError {
    public InvalidParams() {super(400, "Invalid params!");}
}
