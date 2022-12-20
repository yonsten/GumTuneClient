package rosegold.gumtuneclient.utils;

import cc.polyfrost.oneconfig.events.event.LocrawEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.HypixelUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kotlinx.serialization.SerializationException;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LocationUtils {
    private final Gson gson = new Gson();

    public enum Island {
        PRIVATE_ISLAND("Private Island"),
        THE_HUB("Hub"),
        THE_PARK("The Park"),
        THE_FARMING_ISLANDS("The Farming Islands"),
        SPIDER_DEN("Spider's Den"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        GOLD_MINE("Gold Mine"),
        DEEP_CAVERNS("Deep Caverns"),
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        JERRY_WORKSHOP("Jerry's Workshop"),
        DUNGEON_HUB("Dungeon Hub"),
        LIMBO("UNKNOWN"),
        LOBBY("PROTOTYPE"),
        DUNGEON("Dungeon");

        private final String name;

        public String getName() {
            return name;
        }

        Island(String name) {
            this.name = name;
        }
    }

    public static Island currentIsland;
    public static boolean onSkyblock = false;

    // TEMPORARY FIX FOR LOCRAW
    // I hope its really temporary
    @SubscribeEvent(receiveCanceled = true)
    public void onChat(ClientChatReceivedEvent event) {
        String unformatted = event.message.getUnformattedText();
        if (!unformatted.startsWith("{") || !unformatted.endsWith("}") || !HypixelUtils.INSTANCE.isHypixel()) return;

        try {
            JsonObject obj = gson.fromJson(unformatted, JsonObject.class);
            if (!obj.has("gametype") || !obj.has("map")) return;

            if (obj.getAsJsonPrimitive("gametype").getAsString().equals("limbo")) {
                if (obj.getAsJsonPrimitive("server").getAsString().equals("limbo")) currentIsland = Island.LIMBO;
                else currentIsland = Island.LOBBY;
            } else {
                onSkyblock = obj.getAsJsonPrimitive("gametype").getAsString().equals("SKYBLOCK");
                if (onSkyblock)
                    for (Island island : Island.values())
                        if (obj.getAsJsonPrimitive("map").getAsString().equals(island.getName())) {
                            currentIsland = island;
                            break;
                        }
            }
            // testing ModUtils.sendMessage("Current island: " + currentIsland.name + "  locraw: " + unformatted);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onLocraw(LocrawEvent event) {
        if (event.info.getGameMode().equals("lobby")) {
            if (event.info.getServerId().equals("limbo")) {
                currentIsland = Island.LIMBO;
            } else {
                currentIsland = Island.LOBBY;
            }
        } else {
            for (Island island : Island.values()) {
                if (event.info.getMapName().equals(island.getName())) {
                    currentIsland = island;
                    break;
                }
            }
        }
    }
}