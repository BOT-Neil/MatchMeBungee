package za.botneil.bungeecore;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PluginMessage;
import za.botneil.bungeecore.Commands.matchme;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MatchME extends Plugin implements Listener {
    private Configuration config;
    public static TreeMap<String, TreeMap<ServerStatus, String>> groupMap = new TreeMap<>();
    public static TreeMap<String, ServerStatus> groupMap2 = new TreeMap<>();
    public static Map<String,Integer> playercount = new HashMap<>();
    public static ArrayList<String> motd;
    public static String[] serverList;
    public MatchME() throws IOException {
    }

    @Override
    public void onEnable() {
        getProxy().registerChannel("matchme:matchme");
        getProxy().getPluginManager().registerListener(this,this);
        getLogger().info("MatchMeBungee Loaded");
        try {
            this.saveDefaultConfig();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        this.reloadConfig();

        this.config.getSection("groups").getKeys().forEach(x->{
            if(!x.equals("motd")){
                groupMap.put(x, new TreeMap<>());
                this.config.getSection("groups."+x).getKeys().forEach(y ->{
                    //System.out.println("x:"+x+" y:"+y);
                    //System.out.println(this.config.getSection("groups."+x+"."+y).getString("ip")+this.config.getSection("groups."+x+"."+y).getInt("port"));
                    groupMap.get(x).put(new ServerStatus(y, this.config.getSection("groups."+x+"."+y).getString("ip"), this.config.getSection("groups."+x+"."+y).getInt("port") ), y);
                });
            } else {this.config.getSection("motd").getKeys().forEach((mtd)->{motd.add(mtd); });}
        });
        getProxy().getScheduler().schedule(this,new Pinger(), 50, TimeUnit.MILLISECONDS);
        getProxy().getPluginManager().registerCommand(this, new matchme(this));
    }
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("matchme:matchme")) {
            ByteArrayDataInput byteArray = ByteStreams.newDataInput(event.getData());
            //System.out.println("BCore debug:");
            //matchme
            String mm = byteArray.readUTF();
            String group = byteArray.readUTF();
            String name = byteArray.readUTF();
            getLogger().info(mm);
            getLogger().info(group);
            getLogger().info(name);
            this.matchMe(name,group);
        }
        Server server = (Server) event.getSender();
        ServerInfo info = server.getInfo();
    }
    public void matchMe(String player, String servergroup){
        ProxiedPlayer pp = this.getProxy().getPlayer(player);
        try {
            pp.connect(this.getProxy().getServerInfo(groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparing(ServerStatus::getName).thenComparingInt(ServerStatus::getOnline).reversed()).iterator().next().getName()));
            //pp.connect(this.getProxy().getServerInfo(groupMap.get(servergroup).keySet().stream().filter(ServerStatus::isOpen).sorted(Comparator.comparingInt(ServerStatus::getOnline).reversed().thenComparing(ServerStatus::getName)).iterator().next().getName()));
        }catch (Exception e){
            BaseComponent baseComponent = new TextComponent("No Server Available.");
            pp.sendMessage(ChatMessageType.CHAT, baseComponent);
            //pp.sendMessage("No Server Available.");
        }
    }
    protected Configuration getConfig() {
        return this.config;
    }
    protected void reloadConfig() {
        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException var4) {
            throw new RuntimeException("Unable to load configuration", var4);
        }
    }

    private void saveDefaultConfig() throws Throwable {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Throwable var2 = null;
                Object var3 = null;

                try {
                    InputStream is = this.getResourceAsStream("config.yml");

                    try {
                        FileOutputStream os = new FileOutputStream(configFile);

                        try {
                            ByteStreams.copy(is, os);
                        } finally {
                            if (os != null) {
                                os.close();
                            }

                        }
                    } catch (Throwable var19) {
                        if (var2 == null) {
                            var2 = var19;
                        } else if (var2 != var19) {
                            var2.addSuppressed(var19);
                        }

                        if (is != null) {
                            is.close();
                        }

                        throw var2;
                    }

                    if (is != null) {
                        is.close();
                    }
                } catch (Throwable var20) {
                    if (var2 == null) {
                        var2 = var20;
                    } else if (var2 != var20) {
                        var2.addSuppressed(var20);
                    }

                    throw var2;
                }
            } catch (IOException var21) {
                throw new RuntimeException("Unable to create configuration file", var21);
            }
        }

    }
}
