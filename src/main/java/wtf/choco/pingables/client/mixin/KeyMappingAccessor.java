package wtf.choco.pingables.client.mixin;

import com.mojang.blaze3d.platform.InputConstants.Key;

import java.util.Map;

import net.minecraft.client.KeyMapping;

import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {

    @Accessor("MAP")
    public static Map<Key, KeyMapping> getMap() {
        throw new NotImplementedException();
    }

}
