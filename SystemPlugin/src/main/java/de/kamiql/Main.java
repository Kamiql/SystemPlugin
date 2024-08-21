package de.kamiql;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import de.kamiql.commands.*;
import de.kamiql.setboard.*;
import de.kamiql.util.Broadcaster;
import de.kamiql.util.Welcomer;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Objects;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    private static final MongoClient client = new DataBase("mongodb://localhost:27017/", "Minecraft", "systen_plugin").client();
    private static final MongoCollection<Document> warp_collection = client.getDatabase("Minecraft").getCollection("systen_plugin");
    private static final MongoCollection<Document> swarp_collection = client.getDatabase("Minecraft").getCollection("systen_plugin");

    @Override
    public void onEnable() {
        createConfig(this);
        FileConfiguration config = this.getConfig();

        String string = Objects.requireNonNull(config.getString("DataBase.connection.string")).trim();
        String db = Objects.requireNonNull(config.getString("DataBase.connection.database")).trim();
        String collec = Objects.requireNonNull(config.getString("DataBase.connection.collection")).trim();

        try (DataBase DB = new DataBase(string, db, collec)) {
            registerCommands();
            registerListener();
            startUtils();
            create_Bot();

            registerBasicCommandPP();
            registerAdminCommandPP();

            this.getLogger().log(Level.INFO, "/////////////////////////////////////////////////////////////////");
            this.getLogger().log(Level.INFO, "--------------- SYSTEM -> Version: " + this.getDescription().getVersion() + " --------------");
            this.getLogger().log(Level.INFO, "// Plugin Enabled! Successful DataBase Connection.");
            this.getLogger().log(Level.INFO, "--------------- SYSTEM -> Version: " + this.getDescription().getVersion() + " --------------");
            this.getLogger().log(Level.INFO, "/////////////////////////////////////////////////////////////////");
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "/////////////////////////////////////////////////////////////////");
            this.getLogger().log(Level.SEVERE, "--------------- SYSTEM -> Version: " + this.getDescription().getVersion() + " --------------");
            this.getLogger().log(Level.SEVERE, "// An Error occurred while trying to enable the Plugin! //");
            this.getLogger().log(Level.SEVERE, "// API-Version: " + this.getPluginMeta().getAPIVersion());
            this.getLogger().log(Level.SEVERE, "// Exception: " + e);
            this.getLogger().log(Level.SEVERE, "// ");
            this.getLogger().log(Level.SEVERE, "// TIPP: Überprüfe den Connection String in der config.yml!");
            this.getLogger().log(Level.SEVERE, "// ");
            this.getLogger().log(Level.SEVERE, "--------------- SYSTEM -> Version: " + this.getDescription().getVersion() + " --------------");
            this.getLogger().log(Level.SEVERE, "/////////////////////////////////////////////////////////////////");

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        client.close();
    }

    private void createConfig(Main main) {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        this.saveDefaultConfig();
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("warp")).setExecutor(new warpCommand(this));
        Objects.requireNonNull(getCommand("hat")).setExecutor(new hatCommand(this));
        Objects.requireNonNull(getCommand("vanish")).setExecutor(new vanishCommand(this));
        Objects.requireNonNull(getCommand("heal")).setExecutor(new healCommand(this));
        Objects.requireNonNull(getCommand("feed")).setExecutor(new feedCommand(this));
        Objects.requireNonNull(getCommand("invsee")).setExecutor(new invseeCommand(this));
    }

    public void registerBasicCommandPP() {
        Permission permPack = new Permission("system.permpack.commands.basic");
        permPack.addParent("system.commands.warp", true);
        permPack.addParent("system.commands.hat", true);
        permPack.addParent("system.commands.feed", true);
        permPack.addParent("system.commands.heal", true);
        permPack.addParent("system.commands.invsee", true);

        getServer().getPluginManager().addPermission(permPack);
    }

    public void registerAdminCommandPP() {
        Permission permPack = new Permission("system.permpack.commands.admin");
        permPack.addParent("system.permpack.commands.basic", true);

        permPack.addParent("system.command.heal.bypass", true);
        permPack.addParent("system.command.feed.bypass", true);
        permPack.addParent("system.command.invsee.admin", true);

        getServer().getPluginManager().addPermission(permPack);
    }

    private void registerListener() {
        getServer().getPluginManager().registerEvents(new invseeCommand(this), this);
        getServer().getPluginManager().registerEvents(new Welcomer(this), this);
        getServer().getPluginManager().registerEvents(new onJoin(), this);
    }

    private void startUtils() {
        Broadcaster broadcaster = new Broadcaster(this);
        broadcaster.startBroadcast();
    }

    public static MongoCollection<Document> warp_collection() {
        return warp_collection;
    }

    public static MongoCollection<Document> swarp_collection() {
        return swarp_collection;
    }

    public void create_Bot() {
        JDABuilder builder = JDABuilder.createDefault("MTI2MzgwNjU4NTcxNzc4NDY1Nw.Ga2nKW.eyiNiSeqhHwt0IIpCi5i575Zjp-Ii2w7hns328", EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        ));
        builder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);
        builder.build();
    }

    public static String getPrefix() {
        return "§3SYSTEM §7";
    }
}