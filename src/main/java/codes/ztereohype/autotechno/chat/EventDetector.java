package codes.ztereohype.autotechno.chat;

import codes.ztereohype.autotechno.AutoTechno;
import codes.ztereohype.autotechno.client.Server;
import codes.ztereohype.autotechno.config.AutoTechnoConfig;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class EventDetector {
    public static boolean mineplexStart = false;
    private final Map<Server, List<Pair<Supplier<String>, Event>>> serverMessageEvents = new EnumMap<>(Server.class);

    private final boolean killMessages;
    private final boolean startMessages;
    private final boolean endMessages;

    public EventDetector() {
        this.endMessages = AutoTechnoConfig.getProperty("SendEndMessages").equals("true");
        this.killMessages = AutoTechnoConfig.getProperty("SendKillMessages").equals("true");
        this.startMessages = AutoTechnoConfig.getProperty("SendStartMessages").equals("true");
        initTable();
    }

    private void initTable() {
        serverMessageEvents.put(Server.HYPIXEL, Arrays.asList(
                Pair.of(() -> "Your Overall Winstreak:", Event.END_GAME),
                Pair.of(() -> "1st Place -", Event.END_GAME),
                Pair.of(() -> "1st Killer -", Event.END_GAME),
                Pair.of(() -> " - Damage Dealt -", Event.END_GAME),
                Pair.of(() -> "Winning Team -", Event.END_GAME),
                Pair.of(() -> "1st -", Event.END_GAME),
                Pair.of(() -> "Winners:", Event.END_GAME),
                Pair.of(() -> "Winner:", Event.END_GAME),
                Pair.of(() -> "Winning Team:", Event.END_GAME),
                Pair.of(() -> " won the game!", Event.END_GAME),
                Pair.of(() -> "Top Seeker:", Event.END_GAME),
                Pair.of(() -> "1st Place:", Event.END_GAME),
                Pair.of(() -> "Last team standing!", Event.END_GAME),
                Pair.of(() -> "Winner #1 (", Event.END_GAME),
                Pair.of(() -> "Top Survivors", Event.END_GAME),
                Pair.of(() -> "Winners -", Event.END_GAME),
                Pair.of(() -> "Sumo Duel -", Event.END_GAME),
                Pair.of(() -> "Most Wool Placed -", Event.END_GAME),

                Pair.of(() -> "The game starts in 1 second!", Event.START_GAME),

                Pair.of(() -> "SkyWars Experience (Kill)", Event.KILL),
                Pair.of(() -> "coins! (Final Kill)", Event.KILL)
        ));

        serverMessageEvents.put(Server.BEDWARS_PRACTICE, Arrays.asList(
                Pair.of(() -> "Winners -", Event.END_GAME),
                Pair.of(() -> "Game Won!", Event.END_GAME),
                Pair.of(() -> "Game Lost!", Event.END_GAME),
                Pair.of(() -> "The winning team is", Event.END_GAME),

                Pair.of(() -> "Game starting in 1 seconds!", Event.END_GAME),
                Pair.of(() -> "Game has started!", Event.END_GAME),

                Pair.of(() -> MinecraftClient.getInstance().getSession().getUsername() + " FINAL KILL!", Event.KILL)
        ));

        serverMessageEvents.put(Server.PVPLAND, Arrays.asList(
                Pair.of(() -> "The match has ended!", Event.END_GAME),
                Pair.of(() -> "Match Results", Event.END_GAME),
                Pair.of(() -> "Winner:", Event.END_GAME),
                Pair.of(() -> "Loser:", Event.END_GAME),

                Pair.of(() -> "The match is starting in 1 second.", Event.START_GAME),
                Pair.of(() -> "The match has started!", Event.START_GAME),

                Pair.of(() -> "slain by " + MinecraftClient.getInstance().getSession().getUsername(), Event.KILL)
        ));

        serverMessageEvents.put(Server.MINEMEN, Arrays.asList(
                Pair.of(() -> "Match Results", Event.END_GAME),

                Pair.of(() -> "1...", Event.START_GAME),

                Pair.of(() -> "killed by " + MinecraftClient.getInstance().getSession().getUsername() + "!", Event.KILL)
        ));

        serverMessageEvents.put(Server.MINEPLEX, Arrays.asList(
                Pair.of(() -> "Chat> Chat is no longer silenced.", Event.END_GAME),

                Pair.of(() -> "Death> You killed", Event.KILL)
        ));
    }

    public @Nullable Event scanForEvent(@NotNull String message) {
        Server server = AutoTechno.client.getCurrentServer();
        if (server == null) return null;

        if (server == Server.MINEPLEX) {
            if (message.contains("You have been sent from ") && message.contains(" to Lobby")) {
                mineplexStart = false;
            } else if (message.contains("1st Place")) {
                mineplexStart = true;
            }
        }

        for (Pair<Supplier<String>, Event> pair : serverMessageEvents.get(server)) {
            if (message.contains(pair.getLeft().get())) {
                Event event = pair.getRight();

                if (server == Server.MINEPLEX && event == Event.END_GAME) {
                    mineplexStart = !mineplexStart;
                    return mineplexStart ? Event.START_GAME : Event.END_GAME;
                }

                switch (event) {
                    case KILL:
                        if (this.killMessages) return event;
                        break;
                    case START_GAME:
                        if (this.startMessages) return event;
                        break;
                    case END_GAME:
                        if (this.endMessages) return event;
                        break;
                }
            }
        }
        return null;
    }
}
