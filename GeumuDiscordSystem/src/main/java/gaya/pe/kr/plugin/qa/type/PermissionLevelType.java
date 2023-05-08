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

        public static boolean canAccess(PermissionLevelType playerPermissionLevel, PermissionLevelType requirePermissionLevel) {

            switch ( requirePermissionLevel ) {
                case ADMIN: {
                    return playerPermissionLevel.equals(ADMIN);
                }
                case STAFF: {
                    return playerPermissionLevel.equals(ADMIN) || playerPermissionLevel.equals(STAFF);
                }
                case USER: {
                    return playerPermissionLevel.equals(ADMIN) || playerPermissionLevel.equals(STAFF) || playerPermissionLevel.equals(USER);
                }
            }

            return playerPermissionLevel.equals(requirePermissionLevel);

        }

    }