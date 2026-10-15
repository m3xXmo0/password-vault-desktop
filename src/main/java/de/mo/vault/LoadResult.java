package de.mo.vault;

import java.util.List;

/**
 * Ergebnis beim Laden: der Tresor und evtl. Warnungen
 * (z.B. Übersprungene Einträge). Die Oberfläche entscheidet, wie sie angezeigt werden.
 */
public record LoadResult(Vault vault, List<String> warnings) {

}
