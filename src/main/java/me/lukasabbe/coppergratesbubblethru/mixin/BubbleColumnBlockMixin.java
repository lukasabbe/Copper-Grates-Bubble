package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BubbleColumnBlock.class, remap = false)
public abstract class BubbleColumnBlockMixin {

    @Shadow
    private static boolean canOccupy(Block bubbleColumn, BlockState occupyState) {
        return false;
    }

    @Shadow
    public static void updateColumn(Block bubbleColumn, LevelAccessor level, BlockPos occupyAt, BlockState occupyState, BlockState bubbleSource) {
    }

    @Shadow
    private static BlockState getColumnState(Block bubbleColumn, BlockState belowState, BlockState occupyState) {
        return null;
    }

    @Unique
    private static BlockState getSource(Block bubbleColumn, BlockPos.MutableBlockPos pos, LevelAccessor world){
        BlockState water = world.getBlockState(pos);
        boolean isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(water);
        while (isWaterLoggedGrate){
            pos.move(Direction.DOWN);
            water = world.getBlockState(pos);
            isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(water);
        }
        pos.move(Direction.UP);
        BlockState waterState = getColumnState(bubbleColumn, water, world.getBlockState(pos));
        if (!waterState.is(bubbleColumn)) return Blocks.WATER.defaultBlockState();
        else return waterState;
    }

    @Inject(method = "updateColumn(Lnet/minecraft/world/level/block/Block;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V", at=@At("HEAD"), cancellable = true)
    private static void newUpdate(Block bubbleColumn, LevelAccessor world, BlockPos pos, BlockState water, BlockState bubbleSource, CallbackInfo ci){
        boolean isWaterLogged = ModBlockTags.isAWaterLoggedCopperGrates(water);
        if(canOccupy(bubbleColumn,water) || isWaterLogged){
            BlockState waterState;
            BlockPos.MutableBlockPos waterPos;
            if(isWaterLogged){
                waterState = getSource(bubbleColumn, pos.mutable(), world);
                waterPos = pos.mutable().move(Direction.UP);
                while (ModBlockTags.isAWaterLoggedCopperGrates(world.getBlockState(waterPos))){
                    waterPos.move(Direction.UP);
                }
                if(!canOccupy(bubbleColumn, world.getBlockState(waterPos))) {
                    ci.cancel();
                    return;
                }
                world.setBlock(waterPos, waterState, 2);
                waterPos.move(Direction.UP);

            }else{
                waterState = getSource(bubbleColumn, pos.below().mutable(),world);
                waterPos = pos.mutable().move(Direction.UP);
                world.setBlock(pos, waterState, 2);
            }
            while(canOccupy(bubbleColumn, world.getBlockState(waterPos))) {
                world.setBlock(waterPos, waterState, 2);
                waterPos.move(Direction.UP);
            }
            if(ModBlockTags.isAWaterLoggedCopperGrates(world.getBlockState(waterPos))){
                BlockPos gratePos = waterPos.immutable();
                updateColumn(bubbleColumn, world, gratePos, world.getBlockState(gratePos), world.getBlockState(gratePos.below()));
            }

            ci.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "canSurvive",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z", ordinal = 0)
    )
    public boolean canPlaceAt(boolean original, @Local(ordinal = 1) BlockState blockState){
        return original || ModBlockTags.isAWaterLoggedCopperGrates(blockState);
    }
}
