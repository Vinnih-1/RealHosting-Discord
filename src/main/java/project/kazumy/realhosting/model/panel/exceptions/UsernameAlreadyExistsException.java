package project.kazumy.realhosting.model.panel.exceptions;

public class UsernameAlreadyExistsException extends Exception {

    public UsernameAlreadyExistsException() {
        super("Este nome de usuário já está registrado por outra pessoa!");
    }
}
