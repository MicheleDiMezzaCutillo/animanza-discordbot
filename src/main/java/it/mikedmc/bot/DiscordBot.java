package it.mikedmc.bot;
import it.mikedmc.bot.commands.DisimpostaCanale;
import it.mikedmc.bot.commands.ImpostaCanale;
import it.mikedmc.bot.commands.Info;
import it.mikedmc.bot.commands.MostraNotificheAttive;
import it.mikedmc.bot.handlers.OnButtonInteractionEventMostraNotificheAttive;
import it.mikedmc.bot.handlers.OnReadyEventMain;
import it.mikedmc.bot.utils.GestoreCanali;
import it.mikedmc.bot.utils.ManagerComandi;
import it.mikedmc.bot.utils.ManagerDatabase;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
public class DiscordBot {

    public static void main(String[] args) {
        new Config();

        if (!ManagerDatabase.createTableIfNotExistsAnime() || !ManagerDatabase.createTableIfNotExistsChannel() || !ManagerDatabase.createTableIfNotExistsNotification()) {
            return;
        }

        GestoreCanali gestore = new GestoreCanali();

        //Bot ds
        JDA jda = JDABuilder.createDefault(Config.animanzaTOKENDS)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new OnReadyEventMain(), new OnButtonInteractionEventMostraNotificheAttive())
                .setEventPassthrough(true)
                .build();

        ManagerComandi managerComandi = new ManagerComandi();
        managerComandi.add(new ImpostaCanale()); // rifatto
        managerComandi.add(new DisimpostaCanale()); // rifatto
        managerComandi.add(new MostraNotificheAttive());
        managerComandi.add(new Info());
        jda.addEventListener(managerComandi);

    }

}
