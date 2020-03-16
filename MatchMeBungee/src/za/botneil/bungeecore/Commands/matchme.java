package za.botneil.bungeecore.Commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import za.botneil.bungeecore.MatchME;

import java.io.IOException;
import java.util.Arrays;

public class matchme extends Command {
    private MatchME me;
    public matchme(MatchME matchME) {
        super("matchme", "matchme.matchme");
    }

    @Override
    public void execute(CommandSender commandSender, String[] string) {
        if (string.length == 2){
            me.matchMe(commandSender.getName(),Arrays.stream(string).skip(1).iterator().next());
        }
    }
}
