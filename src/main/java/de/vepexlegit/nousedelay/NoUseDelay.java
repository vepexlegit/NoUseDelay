package de.vepexlegit.nousedelay;

import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.*;
import java.lang.reflect.*;

@Mod(modid = "nousedelay", version = "1.0", acceptedMinecraftVersions = "[1.8.9]")
public class NoUseDelay {
    private Field itemInUseCountField;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        try {
            this.itemInUseCountField = EntityPlayer.class.getDeclaredField("itemInUseCount");
            this.itemInUseCountField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (this.itemInUseCountField == null || event.player == null) return;
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            if (player.isUsingItem()) {
                ItemStack usingItem = player.getItemInUse();
                int maxDuration = usingItem.getItem().getMaxItemUseDuration(usingItem);
                try {
                    this.itemInUseCountField.setInt(player, maxDuration - 1);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                ItemStack currentItem = player.inventory.getCurrentItem();
                if (!ItemStack.areItemStacksEqual(usingItem, currentItem)) {
                    player.stopUsingItem();
                }
            }
        }
    }
}