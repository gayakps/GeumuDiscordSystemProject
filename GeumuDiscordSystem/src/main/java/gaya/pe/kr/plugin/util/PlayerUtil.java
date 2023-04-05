package gaya.pe.kr.plugin.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtil {

    public static boolean isDieDamage(Player victim, double damage) {
        double victimHealth = victim.getHealth();
        return victimHealth - damage <= 0.0D;
    }

    public static void setHealth(Player player, double health) {
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        player.setHealth(health);
        player.setFoodLevel(20);
    }
    
    public static String getPlayerName(UUID uuid) {

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String playerName = offlinePlayer.getName();
            if ( uuid.equals(offlinePlayer.getUniqueId()) ) {
                return playerName;
            }
        }

        return null;
    }

}
