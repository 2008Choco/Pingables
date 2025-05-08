package wtf.choco.pingables.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class MouseEvent {

    public static final Event<MouseScroll> SCROLL = EventFactory.createArrayBacked(MouseScroll.class,
            listeners -> (horizontalScroll, verticalScroll) -> {
                for (MouseScroll event : listeners) {
                    if (!event.onMouseScroll(horizontalScroll, verticalScroll)) {
                        return false;
                    }
                }

                return true;
            }
    );

    private MouseEvent() { }

    @FunctionalInterface
    public static interface MouseScroll {

        public boolean onMouseScroll(int horizontalScroll, int verticalScroll);

    }

}
