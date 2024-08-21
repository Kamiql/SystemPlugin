package de.kamiql.commands;

import de.kamiql.Main;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record hatCommand(Main plugin) implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        if (args.length == 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemStack helmet = player.getInventory().getHelmet();

            if (item.getType() == Material.AIR) {
                player.sendActionBar(Main.getPrefix() + "Du musst ein Item in der hand haben, um es dir auf den Kopf zu setzten!");
                return false;
            }

            player.getInventory().setHelmet(item);

            if (helmet != null && helmet.getType() != Material.AIR) {
                player.getInventory().setItemInMainHand(helmet);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            player.sendActionBar(Main.getPrefix() + "Du hast dir das Item " + item.getItemMeta().getDisplayName() + " §7auf den Kopf gesetzt!");

            return false;
        }


        return false;
    }
}
