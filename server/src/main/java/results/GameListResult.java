package results;
import model.GameData;

import java.util.Collection;

public record GameListResult(Collection<GameSummary> games) {
}
