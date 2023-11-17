package cc.unilock.maxhealthfixfixfix.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 9999)
public abstract class LivingEntityMixin {
	@Unique
	@Nullable
	private Float actualHealth = null;

	@Shadow
	public abstract float getMaxHealth();

	@Shadow
	public abstract float getHealth();

	@Shadow
	public abstract void setHealth(float health);

	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	private void maxHealthCheck$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if (nbt.contains("Health", NbtElement.NUMBER_TYPE)) {
			final float nbtHealth = nbt.getFloat("Health");

			if (nbtHealth > this.getMaxHealth() && nbtHealth > 0) {
				actualHealth = nbtHealth;
			}
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void maxHealthCheck$tick(CallbackInfo ci) {
		if (actualHealth != null) {
			if (actualHealth > 0 && actualHealth > this.getHealth()) {
				this.setHealth(actualHealth);
			}

			actualHealth = null;
		}
	}
}
