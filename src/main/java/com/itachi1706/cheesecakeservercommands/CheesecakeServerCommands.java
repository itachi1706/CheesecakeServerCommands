package com.itachi1706.cheesecakeservercommands;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.commands.MainCommand;
import com.itachi1706.cheesecakeservercommands.reference.CommandPermissionsLevel;
import com.itachi1706.cheesecakeservercommands.reference.References;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(References.MOD_ID)
public class CheesecakeServerCommands
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static HashMap<String, DamageSource> knownDamageSources;
    private static ArrayList<BaseCommand> commands = new ArrayList<>();

    public CheesecakeServerCommands()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");


    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Some example code to dispatch IMC to another mod
        InterModComms.sendTo(References.MOD_ID, "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // Register commands here

    /**
     * Register Commands here and set their permission level
     *
     * <a href="https://minecraft.fandom.com/wiki/Permission_level">Permission Levels</a> (see {@link CommandPermissionsLevel})
     * 0 - Normal Player
     * 1 - Player can bypass spawn protection.
     * 2 - Player or executor can use more commands (see Commands) and player can use command blocks. (Cheat commands)
     * 3 - Player or executor can use more commands. (Multiplayer management)
     * 4 - Player or executor can use all the commands. (Server management)
     *
     * From Command Code checkPermission
     * return true -> 0
     * return isOperatorOrConsole -> 2
     *
     * @param event Register Commands Event
     */
    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        LOGGER.info("Starting command registration");

        // Add commands
        commands.add(new MainCommand("csc", CommandPermissionsLevel.ALL, true));
        commands.add(new MainCommand("cheesecakeservercommands", CommandPermissionsLevel.ALL, true));


        // Register to Forge
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        commands.forEach((cmd) -> {
            if (cmd.setExecution() != null) {
                LOGGER.info("Added command: " + cmd.getName());
                dispatcher.register(cmd.getBuilder());
            }
        });
        LOGGER.info("Command registration complete");
    }

    // Server stopping event. Save stuff here
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        LOGGER.info("HELLO from server stopping");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
//    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//    public static class RegistryEvents
//    {
//        @SubscribeEvent
//        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
//        {
//            // Register a new block here
//            LOGGER.info("HELLO from Register Block");
//        }
//    }
}
