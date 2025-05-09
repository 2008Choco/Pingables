package wtf.choco.pingables.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.KeyMapping;

public final class RawInputEvent {

    public static final Event<KeyMappingStateChange> KEY_MAPPING_STATE_CHANGE = EventFactory.createArrayBacked(KeyMappingStateChange.class,
            listeners -> (key, down) -> {
                for (KeyMappingStateChange event : listeners) {
                    event.onStateChange(key, down);
                }
            }
    );

    public static final Event<MouseScroll> MOUSE_SCROLL = EventFactory.createArrayBacked(MouseScroll.class,
            listeners -> (horizontalScroll, verticalScroll) -> {
                for (MouseScroll event : listeners) {
                    if (!event.onMouseScroll(horizontalScroll, verticalScroll)) {
                        return false;
                    }
                }

                return true;
            }
    );

    private RawInputEvent() { }

    @FunctionalInterface
    public static interface KeyMappingStateChange {

        public void onStateChange(KeyMapping key, boolean down);

    }

    @FunctionalInterface
    public static interface MouseScroll {

        public boolean onMouseScroll(int horizontalScroll, int verticalScroll);

    }

}
