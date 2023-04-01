package gaya.pe.kr.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class UtilMethod {

    public static String removeColor(String s) {
        return s.replaceAll("(§|&)[0-9A-FK-ORa-fk-or]", "");
    }

    public static String removeChar(String paramString) {
        return paramString.replaceAll("[^0-9]", ""); // 숫자를 제외하고 전부 제거
    }

    public static int getHasPlayerItemAmount(ItemStack targetItemStack, PlayerInventory playerInventory) {
        int amount = 0;
        ItemMeta targetItemStackItemMeta = targetItemStack.getItemMeta();
        for (ItemStack playerItemStack : playerInventory.getStorageContents()) {
            if ( playerItemStack != null ) {
                if (playerItemStack.getType().equals(targetItemStack.getType())) { // 같은 Material 이고
                    if (playerItemStack.getDurability() == targetItemStack.getDurability()) { // 같은 내구도이며 ( 만일 데이터가 있는 아이템일수도 있으니까 )
                        amount = amount + getPlayerItemAmountAsEqualTargetItem(playerItemStack, targetItemStackItemMeta);
                    }
                }
            }
        }
        return amount;
    }

    /**
     * 해당 메서드는 이름을 비교하여 아이템의 유무를 정확히 찾아내는 메서드다
     * @param playerItemStack 플레이어의 현재 아이템
     * @param targetItemStackItemMeta 비교 대상 아이템 Meta
     * @return
     */
    public static int getPlayerItemAmountAsEqualTargetItem(ItemStack playerItemStack, ItemMeta targetItemStackItemMeta) {
        ItemMeta playerItemMeta = playerItemStack.getItemMeta();
        int amount = 0;
        if (targetItemStackItemMeta.hasDisplayName() && playerItemMeta.hasDisplayName()) { // 둘 다 이름이 존재하고
            if (targetItemStackItemMeta.getDisplayName().equals(playerItemMeta.getDisplayName())) { // 이름이 같네?
                amount = playerItemStack.getAmount();
            }
        } else if (!targetItemStackItemMeta.hasDisplayName() && !playerItemMeta.hasDisplayName()) { // 둘 다 존재하지 않는다면
            amount = playerItemStack.getAmount();
        }
        return amount;
    }

    public static Player getOnlinePlayer(String targetPlayerName) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if ( onlinePlayer != null ) {
                if ( onlinePlayer.isOnline() ) {
                    String onlinePlayerName = UtilMethod.removeColor(onlinePlayer.getName());
                    if (targetPlayerName.equals(onlinePlayerName)) {
                        return onlinePlayer;
                    }
                }
            }
        }
        return null;
    }

    public static String getItemDisplay(ItemStack targetItem) {

        String displayName = "";
        if ( !Filter.isNullOrAirItem(targetItem) ) {
            if (targetItem.hasItemMeta()) {
                ItemMeta itemMeta = targetItem.getItemMeta();
                if (itemMeta != null) {
                    if (itemMeta.hasDisplayName()) {
                        displayName = itemMeta.getDisplayName();
                    }
                }
            }
        }

        return displayName;
    }

    public static String getOneLineString(String[] args, int startIndex) {
        if ( args.length-1 >= startIndex ) {
            StringBuilder stringBuilder = new StringBuilder();
            for ( ; startIndex <= args.length-1; startIndex++ ) {
                stringBuilder.append(args[startIndex]).append(" ");
            }
            return stringBuilder.toString().replace("&","§").trim();
        }
        return "";
    }

    public static boolean giveReward(Player player, ItemStack[] rewardContents) {
        if (rewardContents != null) {
            for (ItemStack itemStack : rewardContents) {
                if (itemStack != null) {
                    if (!itemStack.getType().equals(Material.AIR)) {
                        player.getInventory().addItem(itemStack);
                    }
                }
            }
            return true;
        }
        return false;
    }


    public static int deletePlayerItem(ItemStack targetItemStack, int amount, PlayerInventory playerInventory) {
        // targetItemStack 의 아이템을 amount 만큼 제거 해줍니다
        int result = 0;
        ItemMeta targetItemMeta = targetItemStack.getItemMeta();
        for ( int i = 0; i <= playerInventory.getStorageContents().length-1; i++ ) {
            ItemStack playerItemStack = playerInventory.getStorageContents()[i];
            if ( amount > 0 ) {
                if (playerItemStack != null) { // 플레이어의 아이템이 Not null 이고
                    if (playerItemStack.getType().equals(targetItemStack.getType())) { // 같은 Type 의 material 이고
                        if (playerItemStack.getDurability() == targetItemStack.getDurability()) { // 같은 내구도이며 ( 만일 데이터가 있는 아이템일수도 있으니까 )
                            int hasAmount = getPlayerItemAmountAsEqualTargetItem(playerItemStack, targetItemMeta);
                            if (hasAmount != 0) { // 이름이 같을 때
                                if (hasAmount >= amount) { // 가지고 있는게 삭제 대상보다 많다면
                                    playerItemStack.setAmount(hasAmount - amount);
                                    result = result + amount;
                                } else {
                                    result = result + hasAmount; // 결과값에 15 더 해 주고
                                    playerItemStack.setAmount(0); // 무조건 0으로 만들어주고
                                }
                                amount = amount - hasAmount;
                            }
                        }
                    }
                }
            } else {
                // 제거 Amount 가 0 보다 작다면 breaking point 를 넣어준다.
                break;
            }
        }
        return result;
    }

    public static int getPlayerRemainInventory(ItemStack[] itemStacks) {
        int count = 0;
        for (ItemStack content : itemStacks) {
            if ( content == null ) {
                count++;
            } else {
                if ( content.getType().equals(Material.AIR) ) {
                    count++;
                }
            }
        }
        return count;
    }

    public static Location getLocation(String locationStr) {
        String[] locationData = locationStr.split(",");
        String worldName = locationData[0];
        double x = Double.parseDouble(locationData[1]);
        double y = Double.parseDouble(locationData[2]);
        double z = Double.parseDouble(locationData[3]);
        float yaw = Float.parseFloat(locationData[4]);
        float pitch = Float.parseFloat(locationData[5]);
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public static String getLocationStr(Location location) {
        return String.format("%s,%f,%f,%f,%f,%f", location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static int getRandomRangeValue(int minSize, int maxSize) {
        return (int) (Math.random() * (maxSize - minSize + 1)) + minSize;
    }



}
