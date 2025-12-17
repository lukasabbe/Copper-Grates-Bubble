package me.lukasabbe.coppergratesbubblethru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.lukasabbe.coppergratesbubblethru.tags.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
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
    private static boolean canExistIn(BlockState state) {
        return false;
    }

    @Shadow
    private static BlockState getColumnState(BlockState state) {
        return null;
    }

    @Shadow
    public static void updateColumn(LevelAccessor world, BlockPos pos, BlockState bubbleSource) {
    }

    @Unique
    private static BlockState getSource(BlockPos.MutableBlockPos pos, LevelAccessor world){
        BlockState water = world.getBlockState(pos);
        boolean isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(water);
        while (isWaterLoggedGrate){
            pos.move(Direction.DOWN);
            water = world.getBlockState(pos);
            isWaterLoggedGrate = ModBlockTags.isAWaterLoggedCopperGrates(water);
        }
        return getColumnState(water);
    }

    @Inject(method = "updateColumn(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V", at=@At("HEAD"), cancellable = true)
    private static void newUpdate(LevelAccessor world, BlockPos pos, BlockState water, BlockState bubbleSource, CallbackInfo ci){
        boolean isWaterLogged = ModBlockTags.isAWaterLoggedCopperGrates(water);
        if(canExistIn(water) || isWaterLogged){
            BlockState waterState;
            BlockPos.MutableBlockPos waterPos;
            if(isWaterLogged){
                waterState = getSource(pos.mutable(), world);
                waterPos = pos.mutable().move(Direction.UP);
                while (ModBlockTags.isAWaterLoggedCopperGrates(world.getBlockState(waterPos))){
                    waterPos.move(Direction.UP);
                }
                if(!canExistIn(world.getBlockState(waterPos))) {
                    ci.cancel();
                    return;
                }
                world.setBlock(waterPos, waterState, 2);
                waterPos.move(Direction.UP);

            }else{
                waterState = getSource(pos.below().mutable(),world);
                waterPos = pos.mutable().move(Direction.UP);
                world.setBlock(pos, waterState, 2);
            }
            while(canExistIn(world.getBlockState(waterPos))) {
                world.setBlock(waterPos, waterState, 2);
                waterPos.move(Direction.UP);
            }
            if(ModBlockTags.isAWaterLoggedCopperGrates(world.getBlockState(waterPos))){
                updateColumn(world,waterPos.above(),world.getBlockState(waterPos));
            }

        }
        ci.cancel();
    }

    @ModifyExpressionValue(
            method = "canSurvive",
            at= @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z", ordinal = 0)
    )
    public boolean canPlaceAt(boolean original, @Local(ordinal = 1) BlockState blockState){
        return original || ModBlockTags.isAWaterLoggedCopperGrates(blockState);
    }
}
