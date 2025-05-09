package wtf.choco.pingables.client.events;

import wtf.choco.pingables.client.PingablesModClient;
import wtf.choco.pingables.client.event.MouseEvent;
import wtf.choco.pingables.client.render.IdentifiedLayerPingTypeSelector;

public final class MouseScrollPingWheelCallback implements MouseEvent.MouseScroll {

    private final PingablesModClient mod;

    public MouseScrollPingWheelCallback(PingablesModClient mod) {
        this.mod = mod;
    }

    @Override
    public boolean onMouseScroll(int horizontalScroll, int verticalScroll) {
        IdentifiedLayerPingTypeSelector selector = mod.getPingTypeSelector();
        if (!selector.isVisible()) {
            return true;
        }

        int page = selector.getPage();
        if (page < selector.getMaxPage() && verticalScroll <= -1) {
            selector.setPage(page + 1);
        } else if (page > 0 && verticalScroll >= 1) {
            selector.setPage(page - 1);
        }

        return false;
    }

}
