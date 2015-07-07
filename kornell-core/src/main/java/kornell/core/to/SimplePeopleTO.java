package kornell.core.to;

import java.util.List;

public interface SimplePeopleTO {

    public static String TYPE = TOFactory.PREFIX + "simplePeople+json";

    List<SimplePersonTO> getSimplePeopleTO();
    void setSimplePeopleTO(List<SimplePersonTO> simplePeopleTO);
}
