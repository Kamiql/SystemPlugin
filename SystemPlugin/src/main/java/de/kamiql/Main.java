package de.kamiql;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import de.kamiql.commands.*;
import de.kamiql.util.Broadcaster;
import de.kamiql.util.Welcomer;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    private static final MongoClient client = new DataBase("", "", "").client();
    private static final MongoCollection<Document> warp_collection = client.getDatabase("KC").getCollection("warps");
    private static final MongoCollection<Document> swarp_collection = client.getDatabase("KC").getCollection("swarps");

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

    public static String getPrefix() {
        return "§3SYSTEM §7";
    }
}