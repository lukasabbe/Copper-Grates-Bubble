package me.lukasabbe.coppergratesbubblethru.mixin;

import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin extends FlowingFluid {

    @Override
    public void tick(ServerLevel world, BlockPos pos, BlockState blockState, FluidState fluidState) {
        var block = world.getBlockState(pos.below());
        boolean isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(block);
        if(isWaterLoggedGrate){
            BlockPos.MutableBlockPos blockPos = pos.mutable();
            while (isWaterLoggedGrate){
                blockPos.move(Direction.DOWN);
                block = world.getBlockState(blockPos);
                isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(block);
            }
            world.scheduleTick(blockPos, world.getBlockState(blockPos).getBlock(), 0);
        }
        super.tick(world, pos, blockState, fluidState);
    }
}
