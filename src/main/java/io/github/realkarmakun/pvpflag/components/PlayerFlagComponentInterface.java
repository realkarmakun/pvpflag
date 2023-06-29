package io.github.realkarmakun.pvpflag.components;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface PlayerFlagComponentInterface extends Component {

    boolean switchState();

    boolean peekState();

    void setState(boolean newState);

}
