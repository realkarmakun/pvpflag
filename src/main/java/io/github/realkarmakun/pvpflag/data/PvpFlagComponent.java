package io.github.realkarmakun.pvpflag.data;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface PvpFlagComponent extends Component {

    boolean switchState();

    boolean peekState();

    void setState(boolean newState);

}
