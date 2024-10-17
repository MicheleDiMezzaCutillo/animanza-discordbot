package it.mikedmc.bot;

import it.mikedmc.MkConfig;
import it.mikedmc.bot.utils.ManagerDatabase;

public class Config extends MkConfig<Config> {

    public Config () {
        super(Config.class);
    }

    public static String animanzaTOKENDS;

    public static String animanzaJdbcUrl;
    public static String mikedmcJdbcUser;
    public static String mikedmcJdbcPassword;
    public static String mikedmcJdbcDriver;


}

// jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:5432/postgres?user=postgres.bdouorwlnxckoqedbary&password=V82ttWGZXiVF81T9