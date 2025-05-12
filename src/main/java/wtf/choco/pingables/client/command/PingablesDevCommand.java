package wtf.choco.pingables.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import java.util.List;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import wtf.choco.pingables.client.PingablesModClient;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PingTypeFilter;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class PingablesDevCommand {

    private static final DynamicCommandExceptionType ERROR_INVALID_PING_TYPE = new DynamicCommandExceptionType(
            key -> Component.literal("Unknown ping type with key \"" + key + "\"")
    );

    private PingablesDevCommand() { }

    @SuppressWarnings("unused") // registryAccess
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess, PingablesModClient mod) {
        dispatcher.register(ClientCommandManager.literal("pingablesdev")
                .then(ClientCommandManager.literal("setfilter")
                    .then(ClientCommandManager.argument("ping_type", ResourceKeyArgument.key(PingablesRegistries.PING_TYPE))
                        .executes(context -> setFilter(context, mod))
                    )
                )
        );
    }

    private static int setFilter(CommandContext<FabricClientCommandSource> context, PingablesModClient client) throws CommandSyntaxException {
        ResourceKey<?> rawKey = context.getArgument("ping_type", ResourceKey.class);
        ResourceKey<PingType> key = rawKey.cast(PingablesRegistries.PING_TYPE).orElseThrow(() -> ERROR_INVALID_PING_TYPE.create(rawKey.location()));

        client.setPingTypeFilter(new PingTypeFilter(List.of(key.location())));
        context.getSource().sendFeedback(Component.literal("Set the filter to be only \"" + key + "\"").withStyle(ChatFormatting.GREEN));
        return 1;
    }

}
