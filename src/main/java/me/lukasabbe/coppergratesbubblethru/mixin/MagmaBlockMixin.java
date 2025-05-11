package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin extends Block {
    public MagmaBlockMixin(Settings settings) {
        super(settings);
    }

    @ModifyExpressionValue(
            method = "getStateForNeighborUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z")
    )
    public boolean updatedGetStateForNeighbor(boolean original, @Local(ordinal = 1, argsOnly = true) BlockState neighborState){
        return original || ModBlockTags.isAWaterLoggedCopperGrates(neighborState);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        BlockPos.Mutable checkUp = pos.mutableCopy().move(Direction.UP);
        boolean aWaterLoggedCopperGrates = ModBlockTags.isAWaterLoggedCopperGrates(world.getBlockState(checkUp));
        if(aWaterLoggedCopperGrates){
            while (aWaterLoggedCopperGrates){
                checkUp.move(Direction.UP);
                aWaterLoggedCopperGrates = ModBlockTags.isAWaterLoggedCopperGrates(world.getBlockState(checkUp));
            }
            world.scheduleBlockTick(checkUp,world.getBlockState(checkUp).getBlock(),0);
        }
        super.onBroken(world, pos, state);
    }
}
