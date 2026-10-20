# Changelog - CobbleFarm

Tutte le modifiche rilevanti alla mod **CobbleFarm** verranno documentate in questo file.

Il formato si basa su [Keep a Changelog](https://keepachangelog.com/it/1.0.0/),
e questo progetto aderisce al [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.6.1] - 2026-10-20

### Risolto
- **Cattura Pokémon con Proprietario & Duplicazione Infinita**: Vietata la cattura di Pokémon appartenenti a giocatori (sia propri sia di altri giocatori), a NPC allenatori o legati a blocchi pascolo (pasture). Eliminato alla radice l'exploit che consentiva di evocare ripetutamente i Pokémon della propria squadra per generare Farm Ball infinite senza mai consumare il Pokémon originale.
- **Protezione da Race Condition & Cattura Multipla**: Introdotti controlli server-side rigorosi sullo stato dell'entità (`isRemoved`, `isAlive`, `queuedToDespawn`, `isBusy`, `isBattling`) e rimozione atomica immediata per prevenire duplicazioni tramite click simultanei nello stesso tick o interazioni a due mani.
- **Inserimento GUI e Controllo Blocchi CobbleFarm**: Bloccato l'inserimento manuale e tramite shift-click delle Farm Ball contenenti specie non consentite in `CobbleFarmScreenHandler`, con messa in pausa automatica dell'operatività del blocco qualora contenga un Pokémon disabilitato dalla configurazione.

### Aggiunto
- **Restrizioni Configurabili per Cattura e Farm**: Introdotto il file di configurazione `config/cobblefarm.json` con opzioni per abilitare/disabilitare Pokémon Leggendari (`allowLegendaryCapture`, default `false`), Mitici (`allowMythicalCapture`, default `false`) e Ultra Creature (`allowUltraBeastCapture`, default `false`), lista di etichette/label vietate (`disallowedLabels`) e blacklist/whitelist personalizzate per specie.
- **Messaggi di Feedback Localizzati**: Aggiunti messaggi di avviso tradotti in tutte le lingue supportate quando si tenta di catturare Pokémon propri, di altri giocatori, al pascolo, Leggendari/Mitici o disabilitati dalla configurazione.

---

## [0.6.0] - 2026-10-15

### Risolto
- **Compatibilità con Mod di Storage (Sophisticated Storage, Drawers, ecc.)**: Aggiornato il sistema di output nei contenitori per utilizzare la Fabric Transfer API (`ItemStorage.SIDED`) con fallback automatico agli inventari vanilla. I blocchi CobbleFarm ora riconoscono ed estraggono correttamente i drop nei barili/casse di Sophisticated Storage, Storage Drawers e altri contenitori moddati.
- **Sincronizzazione GUI & Tasto Pausa**: Migrato lo screen handler all'`ExtendedScreenHandlerType` di Fabric sincronizzando il codec del `BlockPos` allo schermo del client. Risolto il funzionamento del pulsante di Pausa ed eliminati i problemi di desync e crash della GUI.
- **Ottimizzazione Rendering Entità nella Schermata**: Ottimizzato il rendering del Pokémon all'interno della GUI di CobbleFarm tramite chiave cache dedicata (`cachedPokemonKey`), evitando la continua re-istanziazione dell'entità e i blocchi del client quando cambia la durabilità della sfera.
- **Protezione NullPointerException nella Generazione Drop**: Fornito un contesto Pokémon valido in `PokemonLootHelper` per prevenire errori NPE durante il calcolo dei drop condizionali di Cobblemon.

### Aggiunto
- **Buffer di Output Interno**: Aggiunto un buffer di memoria interna in `CobbleFarmBlockEntity` per trattenere temporaneamente i drop nel caso in cui il contenitore sottostante sia pieno o momentaneamente inaccessibile, prevenendo la perdita di oggetti.
- **Sicurezza e Protezioni della Farm Ball**: Bloccata la cattura di Pokémon appartenenti ad altri giocatori, Pokémon NPC o Pokémon attualmente impegnati in una lotta, con appositi messaggi di avviso localizzati.
- **Salvataggio Held Item**: I Pokémon catturati espellono automaticamente nell'inventario del giocatore o a terra gli strumenti che tenevano assegnati (come Avanzi, Master Ball, ecc.) senza distruggerli.
- **Preservazione NBT Completa**: I dati del Pokémon nella Farm Ball vengono ora salvati tramite `saveToNBT` con il `RegistryManager` per garantire la conservazione totale di IV, EV, mosse e componenti.
- **Ricetta Forgiatura Netherite**: Aggiornata la ricetta per la Farm di Netherite utilizzando il template per forgiatura di Minecraft 1.21 (`minecraft:smithing_transform`).
- **Animazione 3D Continua nel Mondo**: Aggiunta una rotazione fluida a 360° per il modello del Pokémon mostrato all'interno del blocco CobbleFarm.
- **Tooltip Usi Rimanenti**: Visualizzazione del conteggio degli usi rimanenti nei tooltip della Farm Ball.
- **Workflow di Pubblicazione Automatizzata**: Configurato `mod-publish-plugin` e il workflow di GitHub Actions per automatizzare i rilasci su CurseForge e GitHub.

---

## [0.5.1] - 26-05-2026
### Aggiunto
- **Localizzazione**: Aggiunti i file di traduzione per le lingue Tedesco, Spagnolo, Francese, Portoghese, Russo e Cinese.
- **Badge di CurseForge**: Aggiunto il badge per il download su CurseForge nel README del progetto.
- **Compatibilità**: Aggiunta la sezione sulla compatibilità e sul supporto per i modpack nel README.

### Modificato
- **Texture GUI**: Aggiornata la texture dello sfondo della schermata GUI di CobbleFarm.
- **Struttura del Progetto**: Aggiornate le regole di gitignore e configurato gitattributes per la normalizzazione dei fine riga (line endings).

## [0.5.0] - 22-05-2026
### Aggiunto
- **Tooltip Dinamici**: Aggiunti tooltip per la Farm Ball sia quando è vuota che quando contiene un Pokémon.
- **Tag di Estrazione Blocchi**: Registrati i blocchi CobbleFarm nel tag `minecraft:mineable/pickaxe` e impostati i requisiti del livello degli strumenti:
  - Strumento in pietra richiesto per la Farm in Ferro.
  - Strumento in ferro richiesto per le Farm in Oro, Diamante e Smeraldo.
  - Strumento in diamante richiesto per la Farm in Netherite.
- **Loot Table dei Blocchi**: Aggiunte loot table per i blocchi delle farm per consentire il rilascio del blocco stesso quando estratto con lo strumento corretto.
- **Icona della Mod**: Aggiunto l'asset ufficiale per l'icona della mod (`logo.png`).

### Risolto
- **Compatibilità Ricette Minecraft 1.21**: Spostate e formattate le ricette nella nuova struttura della cartella `recipe` per la 1.21, utilizzando il campo `"id"` invece di `"item"` nei risultati delle ricette.

## [0.4.0] - 20-05-2026
### Aggiunto
- **Sistema di Usura**: Le Farm Ball ora hanno una durabilità di 256 utilizzi e subiscono danni a ogni ciclo di produzione in base al livello della farm (i livelli superiori riducono la probabilità di danno).
- **Renderer 3D Dinamico del Blocco**: Implementato `CobbleFarmRenderer` per renderizzare il Pokémon catturato visivamente all'interno del blocco nel mondo di gioco.
- **Modello Base Centralizzato**: Introdotto il modello di blocco `cobblefarm_preset` come genitore comune per tutti i modelli dei vari tier.
- **Texture Preset CobbleFarm**: Aggiunta una texture base condivisa per il modello di blocco preset.

### Modificato
- **Ristrutturazione Modelli**: Aggiornati i modelli di blocchi e oggetti di tutti i tier per ereditare dal modello centralizzato `cobblefarm_preset`.
- **Pulizia del Progetto**: Ottimizzati i file di compilazione Gradle, riorganizzato `gradle.properties` e pulito il file `.gitignore`.

### Risolto
- **Generazione Loot Deterministica**: Sostituita l'istanza statica di Random con `world.getRandom()` in `PokemonLootHelper`.
- **Limiti di Render dell'Entità nello Schermo**: Corretti i limiti di rendering dell'entità Pokémon e la posizione del testo di stato nell'interfaccia di CobbleFarm.
- **Logica degli Slot Inventario**: Impedito l'inserimento di un secondo Pokémon in uno slot Farm Ball già occupato nell'interfaccia utente.
- **Codec del Blocco Tier**: Corretto il salvataggio del tier di ciascuna variante di blocco utilizzando un codec a livello di istanza.

## [0.3.0] - 18-05-2026
### Aggiunto
- **Nuova Struttura degli Asset**: Riorganizzate e aggiunte le texture (`cobblefarm.png`, `mycelium.png`, `glass.png`).
- **Modelli Preset Puliti**: Introdotti i modelli aggiornati per `farm_preset.json` e `farm_ball.json`.

### Modificato
- **Rinomina di Namespace e Asset**: Rinominate tutte le definizioni dei modelli di blocchi e oggetti dal prefisso `pokemon_farm_*` al prefisso `cobblefarm_*`.

### Rimosso
- **Asset Inutilizzati**: Rimossi i vecchi asset obsoleti come `pokemon_farm.png`, le vecchie texture per i tier delle farm (`farm_iron.png`, `farm_gold.png`, ecc.), `captured_pokemon_item.png`, `farm_ball_model.json` e le vecchie configurazioni dei mixin.

## [0.2.0] - 15-05-2026
### Modificato
- **Migrazione alle Mappature Yarn**: Migrato l'intero codice sorgente (blocchi, oggetti, entità di blocco, pacchetti di rete, helper per i drop, mixin e schermate GUI) alle mappature Yarn per una migliore compatibilità nello sviluppo.
- **Pulizia dei Namespace**: Rinominate le classi e risorse principali dal prefisso `PokemonFarm` al namespace `CobbleFarm` (es. `PokemonFarmBlockEntity` -> `CobbleFarmBlockEntity`).
- **Separazione Client**: Spostata la logica esclusiva del client (come il rendering delle schermate e l'entrypoint client) in una cartella sorgente dedicata `src/client`.

## [0.1.0] - 10-05-2026
### Aggiunto
- **Architettura Iniziale della Mod**: Configurazione iniziale del progetto Fabric, script di compilazione Gradle e metadati del manifest.
- **5 Tier di Farm**: Aggiunte le varianti di blocco in Ferro, Oro, Diamante, Smeraldo e Netherite con velocità di produzione configurabili.
- **Oggetto Farm Ball**: Implementato l'oggetto utilizzato per catturare i Pokémon e inserirli nei blocchi farm.
- **Integrazione Loot Table**: Calcolo e generazione automatica dei drop passivi basati sulla loot table nativa (Vanilla o Cobblemon) del Pokémon catturato.
- **Hook di Cattura**: Aggiunto mixin per intercettare gli eventi di cattura delle Pokeball.
- **Interfaccia Utente Interattiva**: Schermate GUI e texture associate che consentono ai giocatori di visualizzare lo stato della cattura, il Pokémon inserito e accedere al proprio inventario.
- **Output Intelligente**: Espulsione automatica del loot generato all'interno di un blocco inventario posizionato direttamente sotto la farm.
- **Ricette Iniziali**: Ricette di fabbricazione per tutti i tier di farm e per la Farm Ball.
- **Localizzazione**: Aggiunta la lingua inglese (`en_us.json`).