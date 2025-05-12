package wtf.choco.pingables.ping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public final record PingTypeFilter(List<ResourceLocation> pingTypes) implements Predicate<ResourceKey<PingType>>, Comparator<ResourceKey<PingType>>, Iterable<ResourceLocation> {

    public static final Codec<PingTypeFilter> CODEC = ResourceLocation.CODEC.listOf().xmap(
            PingTypeFilter::new,
            PingTypeFilter::pingTypes
    );

    public static final StreamCodec<ByteBuf, PingTypeFilter> STREAM_CODEC = ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()).map(
            PingTypeFilter::new,
            PingTypeFilter::pingTypes
    );

    public PingTypeFilter {
        pingTypes = ImmutableList.copyOf(pingTypes);
    }

    public int getIndex(ResourceLocation location) {
        return pingTypes.indexOf(location);
    }

    public int getIndex(ResourceKey<PingType> key) {
        return getIndex(key.location());
    }

    public int getIndex(Holder<PingType> pingType) {
        return pingType.unwrapKey().map(this::getIndex).orElse(-1);
    }

    public boolean contains(ResourceLocation key) {
        return pingTypes.contains(key);
    }

    public boolean contains(ResourceKey<PingType> key) {
        return contains(key.location());
    }

    public boolean contains(Holder<PingType> pingType) {
        return pingType.unwrapKey().map(this::contains).orElse(false);
    }

    public int size() {
        return pingTypes.size();
    }

    public boolean isEmpty() {
        return pingTypes.isEmpty();
    }

    @Override
    public boolean test(ResourceKey<PingType> key) {
        return pingTypes.contains(key.location());
    }

    @Override
    public int compare(ResourceKey<PingType> first, ResourceKey<PingType> second) {
        ResourceLocation firstKey = first.location();
        ResourceLocation secondKey = second.location();
        int compare = Integer.compare(pingTypes.indexOf(firstKey), pingTypes.indexOf(secondKey));
        return compare != 0 ? compare : firstKey.compareTo(secondKey);
    }

    @Override
    public Iterator<ResourceLocation> iterator() {
        return Iterators.unmodifiableIterator(pingTypes.listIterator());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("pingTypes", pingTypes)
                .build();
    }

}
