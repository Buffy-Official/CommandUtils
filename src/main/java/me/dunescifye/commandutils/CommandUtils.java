package me.dunescifye.commandutils;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.jorel.commandapi.CommandAPI;
import me.dunescifye.commandutils.commands.*;
import me.dunescifye.commandutils.files.Config;
import me.dunescifye.commandutils.listeners.BlockDropItemListener;
import me.dunescifye.commandutils.listeners.EntityChangeBlockListener;
import me.dunescifye.commandutils.listeners.EntityDamageByEntityListener;
import me.dunescifye.commandutils.listeners.GodModeListener;
import me.dunescifye.commandutils.placeholders.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.logging.Logger;

public final class CommandUtils extends JavaPlugin {

    private static CommandUtils plugin;
    public static NamespacedKey keyEIID = new NamespacedKey("executableitems", "ei-id");
    public static final NamespacedKey keyNoDamagePlayer = new NamespacedKey("lunaritems", "nodamageplayer");
    public static final NamespacedKey noGravityKey = new NamespacedKey("lunaritems", "nogravity");
    public static final NamespacedKey autoPickupKey = new NamespacedKey("commandutils", "autopickup");
    public static boolean griefPreventionEnabled;
    private static HashMap<String, Command> commands = new HashMap<>();
    /*
    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

     */


    @Override
    public void onEnable() {
        plugin = this;
        Logger logger = plugin.getLogger();

        //Files first
        Config.setup(this);

        registerListeners();
        //CommandAPI.onEnable();

        commands.put("BlockCycle", new BlockCycleCommand());

        for (Command command : commands.values()) {
            command.register();
        }


        //Special Commands
        if (Bukkit.getPluginManager().isPluginEnabled("ExecutableBlocks")) {
        }

        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) {
            logger.info("Detected GriefPrevention, enabling support for it.");
            griefPreventionEnabled = true;
        }

        CustomBlockData.registerListener(plugin);


        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new StringUtils(this).register();
        }

    }

    @Override
    public void onDisable() {
        CommandAPI.unregister("waterlogblock");
        CommandAPI.unregister("bonemealblock");
        CommandAPI.unregister("breakandreplant");
        CommandAPI.unregister("breakinfacing");
        CommandAPI.unregister("breakinradius");
        CommandAPI.unregister("breakinxyz");
        CommandAPI.unregister("removeitemsetvariable");
        CommandAPI.unregister("sendmessage");
        CommandAPI.unregister("setitemnbt");
        CommandAPI.unregister("weightedrandom");
        CommandAPI.unregister("setitem");
        CommandAPI.unregister("highlightblocks");
        CommandAPI.unregister("cobwebprison");
        CommandAPI.unregister("itemattribute");
        CommandAPI.unregister("runcommandlater");
        CommandAPI.unregister("while");
        CommandAPI.unregister("spawnnodamagefirework");
        CommandAPI.unregister("blockgravity");
        CommandAPI.unregister("sendbossbar");
        CommandAPI.unregister("replaceinfacing");
        CommandAPI.unregister("broadcastmessage");
        CommandAPI.unregister("chancerandomrun");
        CommandAPI.unregister("spawnnodamageevokerfang");
        CommandAPI.unregister("spawnblockbreaker");
        CommandAPI.unregister("runwhen");
        CommandAPI.unregister("food");
        CommandAPI.unregister("settntsource");
        CommandAPI.unregister("changevillagerprofession");
        CommandAPI.unregister("loadcrossbow");
        CommandAPI.unregister("raytraceparticle");
        CommandAPI.unregister("launchfirework");

        //CommandAPI.onDisable();
    }

    private void registerListeners() {
        new EntityDamageByEntityListener().entityDamageByEntityHandler(this);
        new EntityChangeBlockListener().entityChangeBlockHandler(this);
        new BlockDropItemListener().blockDropItemHandler(this);
        new GodModeListener().registerEvents(this);
    }
    public static CommandUtils getInstance(){
        return plugin;
    }

    public static void addCommand(String name, Command command){
        commands.put(name, command);
    }
}
