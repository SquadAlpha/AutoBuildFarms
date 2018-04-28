package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.menu;

import lombok.Getter;
import me.lucko.helper.menu.paginated.PaginatedGuiBuilder;

public class PGB {

    public static PaginatedGuiBuilder get(String title) {
        return new RPGB(title).getPgb();
    }

    public static class RPGB {
        @Getter
        private final PaginatedGuiBuilder pgb;

        public RPGB(String title) {
            this.pgb = PaginatedGuiBuilder.create();
            this.pgb.title(title);
        }
    }

}
