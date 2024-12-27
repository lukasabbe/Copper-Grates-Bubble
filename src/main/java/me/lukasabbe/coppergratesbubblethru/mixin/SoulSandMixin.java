package me.lukasabbe.coppergratesbubblethru.mixin;

import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoulSandBlock.class)
public class SoulSandMixin {
    @Inject(method = "scheduledTick", at=@At("RETURN"))
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci){
        if(ModBlockTags.isAWaterLoggedCopperGrates(state)){
            pos = pos.up();
            BubbleColumnBlock.update(world, pos.up(), state);
        }
    }
}
