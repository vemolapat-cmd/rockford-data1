# Rockford: příprava centrální databáze

Tento adresář připravuje databázovou strukturu pro společnou evidenci plateb a dat mezi webem a Android aplikací. Samotné sdílení začne fungovat až po vytvoření Supabase projektu a propojení aplikací s jeho URL a veřejným publishable/anon klíčem.

## Bezpečnost před nasazením

- Aktuální repozitář `rockford-data1` je veřejný a současný `app/src/main/assets/data.json` obsahuje údaje o nájemnících a finančních zůstatcích. Před produkčním použitím je nutné repozitář přepnout na soukromý nebo odstranit citlivé údaje z veřejné historie/repozitáře. Změna viditelnosti může ovlivnit existující odkazy a sestavení, proto se neprovádí automaticky.
- Nikdy nevkládejte Supabase `service_role` klíč do HTML, JavaScriptu ani APK.
- Zásady RLS v `schema.sql` dovolují přístup pouze přihlášeným uživatelům. Před spuštěním je potřeba nastavit autentizaci a povolit přístup jen oprávněným osobám.
- SQL níže vytvoří tabulky, ale samo o sobě aplikaci nepropojí a automaticky nepřenese stávající data.

## Postup nastavení

1. Vytvořte projekt v Supabase a uložte si Project URL a publishable/anon key.
2. V Supabase otevřete SQL Editor a spusťte obsah souboru `schema.sql`.
3. V Authentication nastavte přihlášení pro uživatele Rockfordu; veřejnou registraci vypněte, pokud ji nepotřebujete.
4. Bezpečně přesuňte aktuální data do centrální tabulky `rockford_state` jako jediný záznam s `id = 1`. Záznam musí obsahovat původní strukturu dat JSON (např. `apartments`, `hall`, `rentByMonth`).
5. Teprve potom doplňte do webu a Android aplikace Supabase klienta, přihlášení, načítání a zápis plateb, synchronizaci a offline frontu.
6. Ověřte přístup z obou zařízení, zálohování a chování při výpadku internetu.

## Tabulky

- `rockford_state`: společná hlavní data objektů, nájmů a souhrnů ve sloupci `data` (JSON).
- `manual_payments`: jednotlivé ručně zadané platby včetně data přijetí, měsíce, částky, jednotky a poznámky.

## Co ještě není hotové

- Není nakonfigurován žádný skutečný Supabase projekt.
- Web ani Android aplikace zatím nemají přihlašování nebo připojení k centrální databázi.
- Není provedena migrace dat ani synchronizace offline změn.
- Nepoužívejte tuto přípravu jako potvrzení, že platby už jsou sdílené.
