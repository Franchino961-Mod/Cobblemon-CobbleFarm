# 🏭 Cobblemon CobbleFarm

## 📖 Panoramica

Automatizza la raccolta degli oggetti dei Pokémon! Un addon per Cobblemon che ti permette di farmare i drop dei Pokémon automaticamente utilizzando blocchi di produzione divisi per tier (livelli).

[![Scarica su CurseForge](https://img.shields.io/badge/Scarica_su-CurseForge-orange?style=for-the-badge&logo=curseforge)](https://www.curseforge.com/minecraft/mc-mods/cobblemon-cobblefarm)

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![Version](https://img.shields.io/badge/version-0.5.0-blue.svg)]()
[![Fabric](https://img.shields.io/badge/Fabric-0.16.9-blue.svg)](https://fabricmc.net/)
[![Cobblemon](https://img.shields.io/badge/Cobblemon-1.6.0+-red.svg)](https://cobblemon.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](../LICENSE)

[![en](https://img.shields.io/badge/lang-en-red.svg)](../README.md)
[![it](https://img.shields.io/badge/lang-it-green.svg)](README.it.md)

> 📝 **Changelog**: Vedi [CHANGELOG.it.md](CHANGELOG.it.md) per lo storico delle versioni.

---

## 🌟 Perché usare CobbleFarm?
- **Loot Passivo**: Ottieni i drop specifici dei Pokémon (come lana, pelle o oggetti rari) in modo automatico.
- **Sistemi Potenziabili**: Progredisci attraverso 5 tier (dal Ferro alla Netherite) per una produzione più veloce e una maggiore durabilità.
- **Automazione Bilanciata**: Offre un obiettivo end-game gratificante senza rovinare l'economia grazie a un sistema di usura ben bilanciato.

---

## 🚀 Guida Rapida
1. Crafta un blocco **CobbleFarm** (partendo dal tier Iron/Ferro) e una **Farm Ball**.
2. Cattura un Pokémon usando la Farm Ball facendoci clic destro sopra.
3. Posiziona un blocco inventario (come una Cassa, un Barile o una Tramoggia) a terra.
4. Posiziona il blocco **CobbleFarm** *direttamente sopra* all'inventario.
5. Inserisci la Farm Ball catturata nella CobbleFarm per iniziare a generare i drop!

---

## ✨ Funzionalità Principali
- **5 Tier di Produzione**: Migliora le tue farm dal Ferro fino alla Netherite. Ogni tier produce loot sempre più velocemente.
- **Output Intelligente**: La farm inserisce automaticamente i drop generati nel blocco inventario posto direttamente sotto di essa. Se l'inventario è pieno, la farm si mette in pausa per evitare lag e perdita di oggetti.
- **Sistema di Usura**: Niente dura per sempre! Le Farm Ball hanno 256 utilizzi base. Mentre la farm lavora, la palla subisce danni e alla fine si romperà (facendoti perdere il Pokémon al suo interno).
- **Rendering Dinamico**: Puoi vedere visivamente il Pokémon catturato che si rimpicciolisce e ruota all'interno della Farm Ball!

---

## ⚙️ Tier della Farm e Usura
Il tier della farm determina sia la velocità con cui vengono generati i drop, sia quanti danni subisce la Farm Ball per ogni ciclo di produzione.

| Tier | Velocità | Danno alla Ball per Ciclo |
|------|----------|---------------------------|
| **Ferro (Iron)** | Molto Lenta | Alto (2-3 danni) |
| **Oro (Gold)** | Lenta | Medio (1-2 danni) |
| **Diamante (Diamond)** | Normale | Basso (1 danno fisso) |
| **Smeraldo (Emerald)** | Veloce | Molto Basso (0-1 danni) |
| **Netherite** | Molto Veloce | **Indistruttibile** (0 danni) |

*Consiglio: Aggiornare alla Netherite Farm è caldamente consigliato per i Pokémon più rari, poiché la Farm Ball non si romperà mai!*

---

## 📦 Requisiti
- **Minecraft**: 1.21.1
- **Fabric Loader**: 0.16.9 o superiore
- **Fabric API**: 0.108.0 o superiore
- **Cobblemon**: 1.6.0 o superiore

---

## 📥 Installazione
1. Installa Fabric e le dipendenze richieste.
2. Scarica il file `.jar` di `CobbleFarm`.
3. Inserisci il file nella cartella `mods` della tua installazione di Minecraft.
4. Avvia il gioco!

---

## 🖥️ Comportamento Client/Server
- **Server**: Richiesto. La mod gestisce i blocchi entità e la generazione del loot lato server.
- **Client**: Richiesto. Include modelli 3D personalizzati e il rendering dinamico per la Farm Ball.

---

## 🤝 Compatibilità
- **Cobblemon**: Progettato come addon per Cobblemon, pienamente compatibile con le tabelle di spawn loot dei Pokémon di Cobblemon.
- **Fabric API**: Richiesto.
- **Modpack**: Sei libero di includere e distribuire questa mod in qualsiasi modpack.

---

## ⚠️ Limitazioni Note
- La farm richiede necessariamente un blocco inventario (cassa, barile, ecc.) posizionato direttamente sotto di essa per funzionare. Non lascerà cadere gli oggetti a terra.
- Quando una Farm Ball si rompe per l'usura, il Pokémon al suo interno viene perso permanentemente.

---

## 🛠️ Risoluzione dei Problemi
### La farm non produce drop!
- Controlla che ci sia un blocco inventario (Cassa, Barile, Tramoggia) direttamente sotto la CobbleFarm.
- Assicurati che l'inventario sottostante non sia completamente pieno.
- Verifica che il Pokémon catturato abbia effettivamente una loot table (alcuni Pokémon potrebbero non droppare nulla di natura).

---

## ❓ FAQ
**D: Come faccio a riavere indietro il mio Pokémon dalla Farm Ball?**  
R: Attualmente, una volta che un Pokémon viene catturato in una Farm Ball per essere usato nella farm, viene convertito in un oggetto e non può più essere liberato nel mondo.

**D: Posso usare le tramogge per inserire le Farm Ball?**  
R: La farm è progettata per l'inserimento manuale della Farm Ball in modo da evitare perdite accidentali, ma estrae automaticamente il loot prodotto nel blocco sottostante.

---

## 💬 Supporto e Feedback
Se riscontri problemi o bug, segnalali includendo:
- Versione della mod
- Versione di Minecraft / Fabric / Cobblemon
- Descrizione dettagliata del problema
- Crash log (se presente)

---

## 📄 Licenza
Questa mod è rilasciata sotto [Licenza MIT](../LICENSE). Sentiti libero di includerla nei tuoi modpack!

## 👤 Autore
**Franchino961** — [GitHub](https://github.com/Franchino961-Mod)
