# Rockford – Android aplikace

Základ první Android aplikace vychází z poslední verze Rockford v29 GREEN. Rozhraní je vložené jako lokální HTML, ale data jsou oddělená do `data.json`.

## Jak bude fungovat aktualizace
- aplikace se při spuštění pokusí stáhnout aktuální `rockford-data.json` z centrálního datového zdroje;
- pokud internet není dostupný, použije poslední úspěšně stažená data;
- při změně funkcí/vzhledu se vydá nová APK, ale běžné změny v tabulkách nevyžadují novou instalaci.

## Důležitá poznámka
ChatGPT projekt Rockford nelze z APK přímo číst. Proto je potřeba ještě vytvořit bezpečný datový zdroj (např. jednoduché HTTPS úložiště/API), kam se po aktualizaci projektových Excelů zveřejní pouze zpracovaná data pro aplikaci.

V `MainActivity.kt` je zatím zástupná adresa `YOUR-DATA-SERVER.example`.
