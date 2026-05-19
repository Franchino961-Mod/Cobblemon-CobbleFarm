# Changelog - CobbleFarm

Tutte le modifiche rilevanti alla mod **CobbleFarm** verranno documentate in questo file.

Il formato si basa su [Keep a Changelog](https://keepachangelog.com/it/1.0.0/),
e questo progetto aderisce al [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - Prima Release Ufficiale
### Aggiunto
- **5 Livelli di Farm**: Aggiunte le farm in Ferro, Oro, Diamante, Smeraldo e Netherite per automatizzare i drop dei Pokémon.
- **Farm Ball**: Uno strumento speciale per catturare i Pokémon e inserirli nelle macchine di produzione.
- **Sistema di Usura**: Le Farm Ball hanno ora una durabilità massima di 256 utilizzi e subiranno danni a ogni ciclo di produzione in base al tier della farm. Le farm di livello più alto infliggono meno danni all'oggetto.
- **Output Intelligente**: Le macchine espellono automaticamente il loot generato all'interno di un blocco inventario posizionato direttamente sotto di esse.
- **Modelli 3D Dinamici**: Il Pokémon catturato viene renderizzato dinamicamente all'interno della Farm Ball, visibile sia nell'inventario che quando inserito nella macchina.
- **Integrazione Loot Table**: I drop prodotti vengono letti direttamente dalle loot table naturali del Pokémon (supportando perfettamente i drop originali di Cobblemon e Vanilla).