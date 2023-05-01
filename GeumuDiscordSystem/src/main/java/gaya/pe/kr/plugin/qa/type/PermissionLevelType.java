package gaya.pe.kr.plugin.qa.type;

import org.bukkit.entity.Player;

public enum PermissionLevelType {

        NONE,
        USER,
        STAFF,
        ADMIN;

        public static PermissionLevelType getPermissionLevelType(Player player) {

            if ( player.isOp() ) return ADMIN;

            if ( player.hasPermission("answer.admin") ) return ADMIN;

            if ( player.hasPermission("answer.staff") ) return STAFF;

            if ( player.hasPermission("answer.user")) return USER;

            return NONE;

        }

    }