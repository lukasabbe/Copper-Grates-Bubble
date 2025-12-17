package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MagmaBlock.class)
public class MagmaBlockMixin extends Block {
    public MagmaBlockMixin(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @ModifyExpressionValue(
            method = "updateShape",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z")
    )
    public boolean updatedGetStateForNeighbor(boolean original, @Local(ordinal = 1, argsOnly = true) BlockState neighborState){
        return original || ModBlockTags.isAWaterLoggedCopperGrates(neighborState);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, @NotNull BlockState state) {
        BlockPos.MutableBlockPos checkUp = pos.mutable().move(Direction.UP);
        boolean aWaterLoggedCopperGrates = ModBlockTags.isAWaterLoggedCopperGrates(level.getBlockState(checkUp));
        if(aWaterLoggedCopperGrates){
            while (aWaterLoggedCopperGrates){
                checkUp.move(Direction.UP);
                aWaterLoggedCopperGrates = ModBlockTags.isAWaterLoggedCopperGrates(level.getBlockState(checkUp));
            }
            level.scheduleTick(checkUp,level.getBlockState(checkUp).getBlock(),0);
        }
        super.destroy(level, pos, state);
    }
}
