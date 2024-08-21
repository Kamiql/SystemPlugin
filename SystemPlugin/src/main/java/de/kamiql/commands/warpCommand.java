package de.kamiql.commands;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import de.kamiql.Main;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import de.kamiql.DataBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public record warpCommand(Main instance) implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length >= 1) {
                if (args.length == 2) {
                    String name = args[1].toLowerCase();
                    switch (args[0].toLowerCase()) {
                        case "add" -> {
                            if (player.hasPermission("system.commands.swarp.admin")) {
                                player.sendActionBar(Main.getPrefix() + saveWarp(name, player.getLocation(), player));
                            } else {
                                player.sendActionBar(Main.getPrefix() + "you don´t have permission!");
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        }
                        case "remove" -> {
                            if (player.hasPermission("system.commands.swarp.admin")) {
                                player.sendActionBar(Main.getPrefix() + removeWarp(name, player));
                            } else {
                                player.sendActionBar(Main.getPrefix() + "you don´t have permission!");
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        }
                        case "tp" -> {
                            if (player.hasPermission("system.commands.swarp.admin")) {
                                player.sendActionBar(Main.getPrefix() + tpWarp(name, player));
                            } else {
                                player.sendActionBar(Main.getPrefix() + "you don´t have permission!");
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                        }
                        default -> {
                            player.sendActionBar(Main.getPrefix() + "Wrong usage! §8/warp <set/remove/update/tp <> tp: name> <name>");
                        }
                    }
                    return true;
                } else if (args.length == 1) {
                    player.sendActionBar(Main.getPrefix() + tpWarp(args[0].toLowerCase(), player));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    return true;
                }
            }
        }
        return false;
    }

    public String saveWarp(String name, Location location, Player player) {
        if (!getWarps(player).contains(name)) {
            try {
                Document document = new Document("name", name)
                        .append("location", new Document("world", location.getWorld().getName())
                                .append("x", location.getX())
                                .append("y", location.getY())
                                .append("z", location.getZ())
                                .append("yaw", location.getYaw())
                                .append("pitch", location.getPitch()))
                        .append("uuid", player.getUniqueId().toString())
                        .append("server", player.getServer().toString())
                        .append("time", DataBase.getDateTime());

                Main.warp_collection().updateOne(
                        Filters.eq("name", name),
                        new Document("$set", document),
                        new UpdateOptions().upsert(true)
                );

                return "successful created the warp!";
            } catch (Exception ignored) {
                return "error creating the warp!";
            }
        }
        return "warp already exists!";
    }

    public String removeWarp(String name, Player player) {
        if (!getWarps(player).contains(name)) {
            return "this warp isn't existing!";
        } else if (getWarps(player).contains(name)) {
            try {
                Main.warp_collection().findOneAndDelete(and(eq("name", name), eq("server", player.getServer().toString())));
                return "successful deleted the warp!";
            } catch (Exception ignored) {
                return "error deleting the warp!";
            }
        }
        return null;
    }

    public String tpWarp(String name, Player player) {
        if (getWarps(player).contains(name)) {
            try {
                Document warpData = Main.warp_collection().find(and(eq("name", name), eq("server", player.getServer().toString()))).first();

                if (warpData != null) {
                    Document locationData = (Document) warpData.get("location");
                    World world = Bukkit.getWorld(locationData.getString("world"));

                    if (world != null) {
                        Location location = new Location(
                                world,
                                locationData.getDouble("x"),
                                locationData.getDouble("y"),
                                locationData.getDouble("z"),
                                locationData.getDouble("yaw").floatValue(),
                                locationData.getDouble("pitch").floatValue()
                        );
                        player.teleport(location);
                        return "successful teleported to the warp!";
                    }
                }
            } catch (Exception ignored) {
                return "error teleporting to the warp!";
            }
        }
        return "this warp isn't existing!";
    }

    private ArrayList<String> getWarps(Player player) {
        ArrayList<String> warpNames = new ArrayList<>();
        MongoCollection<Document> collection = Main.warp_collection();
        try (MongoCursor<Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                String warpName = document.getString("name");
                String serverName = document.getString("server");
                if (warpName != null && serverName != null) {
                    if (serverName.equals(player.getServer().toString())) {
                        warpNames.add(warpName.toLowerCase());
                    }
                }
            }
        }
        return warpNames;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();
                if (sender.hasPermission("system.commands.swarp.admin")) {
                    completions.add("add");
                    completions.add("remove");
                    completions.add("tp");
                    return completions;
                } else {
                    return getWarps(player);
                }
            } else if (args.length == 2 && !Objects.equals(args[0], "add")) {
                return getWarps(player);
            }
            return null;
        }
        return null;
    }
}
