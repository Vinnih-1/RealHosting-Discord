package project.kazumy.realhosting.discord.services.panel.exceptions;

public class WrongEmailException extends Exception {

    public WrongEmailException() {
        super("Você precisa digitar um email válido!");
    }
}
