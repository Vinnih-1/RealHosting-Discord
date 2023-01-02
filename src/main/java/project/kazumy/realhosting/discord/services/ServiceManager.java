package project.kazumy.realhosting.discord.services;

import org.reflections.Reflections;
import project.kazumy.realhosting.discord.InitBot;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Logger;

public class ServiceManager {

    public static void loadServices(InitBot initBot) {
        new Reflections("project.kazumy.realhosting.discord.services").getSubTypesOf(BaseService.class)
                .stream()
                .map(service -> {
                    try {
                        return service.getDeclaredConstructor().newInstance().service(initBot.jda);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        Logger.getGlobal().severe("Some service could not be loaded: " + service.getName() + " " + e.getMessage());
                        System.exit(1);
                        return null;

                    }
                }).sorted((Comparator.comparingLong(BaseService::getProperty)))
                .forEach(instance -> {
                    Arrays.stream(initBot.getClass().getDeclaredFields())
                            .filter(field -> field.getType().isAssignableFrom(instance.getClass()))
                            .forEach(field -> {
                                try {
                                    Logger.getGlobal().info(String.format("Starting %s class...", field.getType().getName()));
                                    field.setAccessible(true);
                                    field.set(initBot, instance);
                                } catch (IllegalAccessException e) {
                                    Logger.getGlobal().severe("Some service could not be loaded: " + field.getType().getName() + " " + e.getMessage());
                                    System.exit(1);
                                }
                            });
                });
    }
}
