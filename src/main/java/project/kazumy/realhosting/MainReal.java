package project.kazumy.realhosting;

import project.kazumy.realhosting.discord.InitBot;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainReal {

    public static void main(String[] args) {
        if (args.length == 0) {
            Logger.getGlobal().log(Level.SEVERE, "The token of your bot cannot be null");
            System.exit(1);
            return;
        }

        new InitBot(args[0]);
    }
}
