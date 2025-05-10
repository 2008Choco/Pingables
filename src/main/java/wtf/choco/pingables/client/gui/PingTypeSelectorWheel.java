package wtf.choco.pingables.client.gui;

import java.util.Optional;

import net.minecraft.core.Holder;

import wtf.choco.pingables.ping.PingType;

public interface PingTypeSelectorWheel {

    public void setVisible(boolean visible);

    public boolean isVisible();

    public void setPage(int page);

    public int getPage();

    public int getMaxPage();

    public Optional<Holder<PingType>> getCurrentlyHoveredPingType();

}
