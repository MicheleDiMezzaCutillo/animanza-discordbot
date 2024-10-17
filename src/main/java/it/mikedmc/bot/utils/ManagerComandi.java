package it.mikedmc.bot.utils;

import it.mikedmc.bot.interfacce.InterfacciaComandi;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ManagerComandi extends ListenerAdapter {

    private List<InterfacciaComandi> commands = new ArrayList<>();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        registerCommands(event.getJDA());
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        registerCommands(event.getJDA());
    }

    private void registerCommands(JDA jda) {
        for (Guild guild : jda.getGuilds()) {
            for (InterfacciaComandi command : commands) {
                if (command.getOptions() == null) {
                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
                } else {
                    guild.upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
                }
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Gestisci la richiesta del comando normale
        for (InterfacciaComandi command : commands) {
            if (command.getName().equals(event.getName())) {
                command.execute(event);
                return;
            }
        }
    }

    public void add(InterfacciaComandi command) {
        commands.add(command);
    }

}