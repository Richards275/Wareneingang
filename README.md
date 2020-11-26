# Prüfung des Wareneingangs

## Ein Java/Spring/Vue.js-Programm zur Bearbeitung des Wareneinganges in einem Weltladen. 

### Hintergrund
Während meines Studiums arbeitete ich ehrenamtlich in einem Weltladen. 
Lieferungen neuer Ware wurden anhand der Rechnung auf Vollständigkeit geprüft.

### Funktionalität des Programmes
Von den Lieferanten wird eine csv-Datei mit den Lieferungsdaten hochgeladen.

Lieferanten können die eingelesenen Daten im Portal prüfen und die Lieferung freigeben. 
Fehlerhafte Datensätze werden angezeigt. 

Mitarbeiter*innen des Weltladens können freigegebene Lieferungsdaten an einem Tablet oder Smartphone 
bearbeiten gemäß:
- vorhanden
- andere Menge geliefert (mit Möglichkeit einer Bemerkung)
- fehlend
- Fehler (mit Möglichkeit einer Bemerkung)

Nach der Bearbeitung wird die Lieferung von Mitarbeiter*innen in den Zustand "bearbeitet" gesetzt.
 
Bearbeitete Lieferungen können von den Lieferanten als csv-Datei heruntergeladen werden .   

Neue User\*innen werden mit einer E-Mail begrüßt. 

Passwortänderungen werden mit einer E-Mail an die User*innen bestätigt.

## Installation

### Datenbank
Legen Sie in PostgreSQL eine Datenbank "wareneingang" an. 
Hibernate wird die Tabellen automatisch erzeugen.

Führen Sie das Skript create.sql aus. 
Dieses legt Rollen, den Weltladen als Lieferanten und eine*n 
Admin-User\*in an.

### Umgebungsvariablen
Hinterlegen Sie in den Umgebungsvariablen Ihres Betriebssystems die folgenden Werte 


    POSTGRES_ADMIN_NAME
    POSTGRES_ADMIN_PW   
    MAIL_USERNAME
    MAIL_PW   
als credentials für die PostgreSQL-Datenbank und den EMail-Account, 
von dem die Begrüßungsemails versendet werden.

Hinterlegen Sie 

     TESTING_MAIL_ADDRESS
Alle ausgehenden E-Mails werden an diese E-Mail-Adresse weitergeleitet. 
Dies ist am Anfang erforderlich und später für Testzwecke sehr hilfreich.

### Starten des Programmes    
Starten Sie das Backend in IntelliJ oder mit

    mvn spring-boot:run
und das Frontend im Unterordner /frontend durch 

      npm install
      npm run dev  
Das Frontend ist nun erreichbar unter

    http://localhost:3000/

## Kennenlernen der Funktionalität
- Geben sie im Menüpunkt Login/Logout im Feld Username oder Email ein: admin@admin.de und
klicken Sie auf "Neues Passwort".
- Sie erhalten eine auf die hinterlegte "TESTING_MAIL_ADDRESS" umgeleitete Email mit Zugangsdaten. 
- Klicken sie auf den Button "Passwort ändern" und generieren sie ein neues Passwort.
Sie erhalten eine E-Mail als Bestätigung.
- Loggen Sie sich als user "admin" mit dem neuen Passwort ein 
- Legen Sie im Menüpunkt User für den Weltladen die Mitarbeiterin "Mitarbeiterin" mit der Rolle "mitarbeiterin"
an. Eine Begrüßungsemail mit einem Passwort wird generiert.
- Legen Sie im Menüpunkt Lieferanten den Lieferanten "Gepa" an
- Legen Sie im Menüpunkt Lieferanten für den Lieferanten "Gepa" den Mitarbeiter "Immer Fair" an
mit der Rolle "lieferant". Eine Begrüßungsemail mit einem Passwort wird generiert.
- Loggen Sie sich ein als User "Immer Fair"
- Laden Sie im Menüpunkt "csv hochladen" die Datei wareeingang_mitPreis.csv hoch
- Im Menüpunkt Lieferungen wird die neue Lieferung angezeigt. Geben Sie diese nach Prüfung frei.
- Loggen Sie sich ein als Userin "Mitarbeiterin"
- Im Menüpunkt Lieferungen wird die neue Lieferung angezeigt. Klicken Sie auf bearbeite.
- Bearbeiten Sie die Lieferung (mobile friendly): entweder direkt mit dem grünen Button OK oder indem 
Sie in die betreffende Zeile klicken und im Modal Ok, Fehlt, teilweise geliefert oder Fehler
wählen.
- Der Barcodereader liest barcodeSchokolade.gif.
- Klicken sie auf das Mikrofon und sagen Sie "Bio". Das Wort erscheint im Suchfeld. 
Ggf. versuchen Sie es bitte mehrfach. 
- Im Reiter Bearbeitet stehen die bearbeiteten Positionen. Sie können diese zurück verschieben in den Reiter
Meldung, indem sie auf die Zeile klicken und "Bearbeiten" klicken. 
- Sind alle Positionen bearbeitet, erscheint ein Button "Lieferung als bearbeitet bestätigen". Bestätigen Sie dies.
- Loggen Sie sich ein als User "Immer Fair"
- Laden Sie im Menüpunkt Lieferungen die csv-Datei herunter  
- Lade Sie im Menüpunkt csv hochladen die bewusst fehlerhafte Datei wareeingang_mitPreisFehler.csv hoch.
- Im Menüpunkt Lieferungen können Sie diese fehlerhafte Lieferung prüfen, die Fehlermeldungen als csv herunterladen 
oder die Lieferung löschen

### Tests ausführen

Frontend: im Ordner /frontend

    npm run build
    npm run test (ggf. zweimal)
Backend: IntelliJ oder nur Unit tests mit 

    mvn test

### Deployment mit Docker
Erzeugen Sie ein jar im Unterverzeichnis /target mittels

    mvn clean install 
Führen Sie im root Verzeichnis aus 

    docker-compose up -d
Prüfen Sie den Start des Frontends und Backends ggf. mit 

    docker-compose logs -f
Sie verlassen die Anzeige mit STRG +c.

Gehen Sie in den database container mit

    docker exec -it database bash  
Geben Sie dort ein

    psql wareneingang admin
Die von Hibernate angelegten Tabellen werden Ihnen angezeigt mit

    \dt
Kopieren Sie den Inhalt von create.sql hinein am Prompt.

Verlassen Sie den Container mit

    \q
    exit
Sie erreichen die Anwendung unter  

    http://localhost:3000/

### JAR mit Frontend und Backend
Kommentieren Sie den auskommentierten Teil ein in pom.xml und führen Sie aus 

    mvn clean install (ggf. zweimal)
