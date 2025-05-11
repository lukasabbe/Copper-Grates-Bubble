package me.lukasabbe.coppergratesbubblethru.mixin;

import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin extends FlowableFluid {

    @Override
    public void onScheduledTick(ServerWorld world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        var block = world.getBlockState(pos.down());
        boolean isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(block);
        if(isWaterLoggedGrate){
            BlockPos.Mutable blockPos = pos.mutableCopy();
            while (isWaterLoggedGrate){
                blockPos.move(Direction.DOWN);
                block = world.getBlockState(blockPos);
                isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(block);
            }
            world.scheduleBlockTick(blockPos, world.getBlockState(blockPos).getBlock(), 0);
        }
        super.onScheduledTick(world, pos, blockState, fluidState);
    }
}
