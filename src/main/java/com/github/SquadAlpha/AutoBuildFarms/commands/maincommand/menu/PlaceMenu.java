package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.menu;

import com.github.SquadAlpha.AutoBuildFarms.config.Config;
import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.Farm;
import lombok.Getter;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PaginatedGui;
import me.lucko.helper.menu.paginated.PaginatedGuiBuilder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;

public class PlaceMenu {

    public final ArrayList<Item> items;
    @Getter
    private PaginatedGui realGui;

    public PlaceMenu(Player p, MainMenu mainMenu) {
        PaginatedGuiBuilder builder = PaginatedGuiBuilder.create();
        builder.title(Config.getPlaceMenuTitle());
        this.items = new ArrayList<>();
        for (Farm f : Reference.farmList.values()) {
            MainMenu.clickItem.rClickItem c = new MainMenu.clickItem(f.getDisplayItem())
                    .addhandler(ClickType.LEFT, e -> new PlaceFarmGui(f, e.getWhoClicked(), this).open()).get();
            items.add(c);
        }
        this.realGui = builder.build(p, paginatedGui -> items);
        //Might make another sub menu imposible
        //this.realGui.setFallbackGui(a -> mainMenu);
    }

    public void open() {
        this.getRealGui().open();
    }

    private class PlaceFarmGui extends Gui {
        private final Farm farm;

        public PlaceFarmGui(Farm f, HumanEntity player, PlaceMenu placeMenu) {
            super((Player) player, 1, Config.getPlaceMenuTitle() + "&r:" + f.getFancyName());
            this.farm = f;
            this.setFallbackGui(a -> placeMenu.getRealGui());
        }

        @Override
        public void redraw() {
            if (isFirstDraw()) {
                for (Farm.Size s : farm.getSizes().values()) {
                    this.addItem(new MainMenu.clickItem(s.getDisplayItem())
                            .addhandler(ClickType.LEFT, e -> ((Player) e.getWhoClicked()).performCommand("abf place " + this.farm.getName() + " " + s.getName()))
                            .get());
                }
            }
        }
    }
}
