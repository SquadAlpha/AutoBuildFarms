package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.menu;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.Farm;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.paginated.PaginatedGui;
import me.lucko.helper.menu.paginated.PaginatedGuiBuilder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PlaceMenu extends PaginatedGui {


    public PlaceMenu(Function<PaginatedGui, List<Item>> cont, Player p, MainMenu mainMenu, PaginatedGuiBuilder model) {
        super(cont, p, model);
        this.setFallbackGui(a -> mainMenu);
    }

    public static PlaceMenu get(Player p, MainMenu mainMenu) {
        PaginatedGuiBuilder builder = PGB.get("&6[&4A&3B&2F&1]&bPlace");
        ArrayList<Item> items = new ArrayList<>();
        PlaceMenu pm = new PlaceMenu(g -> items, p, mainMenu, builder);
        for (Farm f : Reference.farmList.values()) {
            MainMenu.clickItem.rClickItem c = new MainMenu.clickItem(f.getDisplayItem())
                    .addhandler(
                            ClickType.LEFT,
                            e -> {
                                PlaceFarmGui g = new PlaceFarmGui(f, e.getWhoClicked(), pm);
                                pm.setFallbackGui(null);
                                g.open();
                            }
                    ).get();
            items.add(c);
        }
        pm.updateContent(items);
        return pm;
    }

    public void open() {
        super.open();
    }

    private static class PlaceFarmGui extends Gui {
        private final Farm farm;

        public PlaceFarmGui(Farm f, HumanEntity player, PlaceMenu placeMenu) {
            super((Player) player, 1, "&6[&4A&3B&2F&1] Place&r:" + f.getFancyName());
            this.farm = f;
            this.setFallbackGui(a -> placeMenu);
        }


        @Override
        public void redraw() {
            if (isFirstDraw()) {
                for (Farm.Size s : farm.getSizes().values()) {
                    this.addItem(new MainMenu.clickItem(s.getDisplayItem())
                            .addhandler(ClickType.LEFT, e -> {
                                ((Player) e.getWhoClicked()).performCommand("abf place " + this.farm.getName() + " " + s.getName());
                                this.setFallbackGui(null);
                                this.close();
                            })
                            .get());
                }
            }
        }
    }
}
