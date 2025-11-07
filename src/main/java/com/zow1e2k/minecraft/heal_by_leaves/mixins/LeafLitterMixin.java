package com.zow1e2k.minecraft.heal_by_leaves.mixins;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(targets = "net.minecraft.world.item.Items$LeafLitterItem")
@Mixin(Item.class)
public class LeafLitterMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {

        Item item = player.getItemInHand(hand).getItem();

        if (item == null) {
            return;
        }

        if (!item.equals(Items.LEAF_LITTER)) {
            return;
        }

        if (player.getHealth() < player.getMaxHealth()) {

            HitResult hitResult = player.pick(5.0D, 0.0F, false);
            if (hitResult.getType() == HitResult.Type.MISS) {

                player.startUsingItem(hand);
                cir.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    public void onGetUseDuration(ItemStack stack, LivingEntity entity, CallbackInfoReturnable<Integer> cir) {
        if (!stack.getItem().equals(Items.LEAF_LITTER)) {
            return;
        }

        cir.setReturnValue(32);
    }

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    public void onGetUseAnimation(ItemStack stack, CallbackInfoReturnable<ItemUseAnimation> cir) {
        if (!stack.getItem().equals(Items.LEAF_LITTER)) {
            return;
        }

        cir.setReturnValue(ItemUseAnimation.EAT);
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    public void onFinishUsingItem(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (!stack.getItem().equals(Items.LEAF_LITTER)) {
            return;
        }

        if (entity.getType() == EntityType.PLAYER) {
            Player player = (Player) entity;

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.CRAFTER_CRAFT, SoundSource.PLAYERS, 1.0F, 1.0F);

            player.heal(1f);

            cir.setReturnValue(stack);
        }
    }
}
