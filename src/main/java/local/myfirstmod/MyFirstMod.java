package local.myfirstmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFirstMod implements ModInitializer {
	public static final String MOD_ID = "myfirstmod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
    public void onInitialize() {
        LOGGER.info("我的第一个模组已成功加载！");
    
        // 监听玩家加入
        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> { // （网络句柄、发送器、服务器实例）
             
            net.minecraft.server.level.ServerPlayer player = handler.getPlayer();
            String playerName = player.getName().getString();

            LOGGER.info("player " + playerName + " join in the server secretly!");
            
            //    player.sendMessage(net.minecraft.network.chat.Component.literal("§a[系统] 欢迎来到这个世界，" + playerName + "！"), false);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§a[系统] 欢迎来到这个世界，" + playerName + "！"));

            // 创建一个包含 1 个钻石的物品堆叠
            // new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND);
            // player.getInventory().add(new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND));

        });

        // 监听玩家破坏方块
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (state.is(net.minecraft.world.level.block.Blocks.DIAMOND_ORE)) {

                 // 强制类型转换，ServerPlayer类型才有sendSystemMessage方法
                net.minecraft.server.level.ServerPlayer serverPlayer = (net.minecraft.server.level.ServerPlayer) player;
                
                // 1. 获取玩家手上拿着的工具
                net.minecraft.world.item.ItemStack handItem = player.getMainHandItem();

                // 2. 检查这个工具是否带有精准采集附魔
                // 在 1.21.4 Mojang 映射表下，通过 RegistryLookup 获取附魔的 Key
                var enchantsLookup = world.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
                net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> silkTouch = enchantsLookup.getOrThrow(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH);

                // 3. 判断如果【没有】精准采集，才给奖励
                if (net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(silkTouch, handItem) == 0) {
                    
                    // 这里放你之前的随机掉落逻辑（world.getRandom().nextFloat() < 0.5f ...）
                    // 如果挖到的是钻石矿，在钻石矿的位置按照概率随机生成一个掉落物
                    if (world.getRandom().nextFloat() < 0.01f) {
                        // 准备一个“物品堆叠” ItemStack
                        net.minecraft.world.item.ItemStack gift = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.DIAMOND_BLOCK);

                        // 在方块所处位置创建以一个掉落物实体 并召唤到世界中
                        // Level 代表的是维度世界， 包括："Overworld", "The Nether", "The End"
                        // 这里的world是监听得到的参数, 但底层类型不是Level, 要进行强制转换
                        net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                            (net.minecraft.world.level.Level) world, 
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, // 加上 0.5 是为了让物品从方块中心蹦出来
                            gift
                        );

                        // 让世界生成这个实体
                        world.addFreshEntity(itemEntity);

                        // 给玩家中奖提示
                        serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§6[幸运] 天哪！你触发了 0.1% 的额外大奖！"));

                    }

                } else {
                    // 也可以温馨提示一下玩家
                    serverPlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c[系统] 带有精准采集的工具无法触发幸运掉落！"));
                }
                                


                
                
                
                
                
                
            }

            return true; // true表示允许玩家正常挖掉这个方块；返回false会让方块变为无敌的
        });
    }

    
}
