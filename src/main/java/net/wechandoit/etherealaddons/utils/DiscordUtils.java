package net.wechandoit.etherealaddons.utils;

import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.RichPresenceButton;
import net.wechandoit.etherealaddons.QuagsireClient;

public class DiscordUtils {

    public static long lastTimestamp = 0L;

    public static void updateRPC(String firstline, String secondline) {
        RichPresence.Builder builder = new RichPresence.Builder();
        builder.setDetails(firstline)
                .setState(secondline)
                .setButtons(new RichPresenceButton[0])
                .setLargeImage("quagsire", "play.etherealskies.com - Mod made by wechandoit");
        builder.setStartTimestamp(lastTimestamp);
        try {
            QuagsireClient.ipcClient.sendRichPresence(builder.build());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
