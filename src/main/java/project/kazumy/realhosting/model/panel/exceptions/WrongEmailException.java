package project.kazumy.realhosting.model.panel.exceptions;

public class WrongEmailException extends Exception {

    public WrongEmailException() {
        super("Você precisa digitar um email válido!");
    }
}
