package project.kazumy.realhosting.discord.services.panel.exceptions;

public class WrongUsernameException extends Exception {

    public WrongUsernameException() {
        super("Seu nome de usuário não pode conter espaços ou caracteres especiais, apenas números e letras!");
    }
}
