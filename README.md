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

Bearbeitete Lieferungen können von den Lieferanten als csv-Datei heruntergeladen werden.

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
Alle ausgehenden E-Mails werden an diese E-Mail-Adresse umgeleitet. Dies ist am Anfang erforderlich und später für
Testzwecke sehr hilfreich.

### Starten des Programmes
Starten Sie das Backend in IntelliJ oder mit

    mvn spring-boot:run
und das Frontend im Unterordner /frontend durch

      npm install
      npm run dev  
Das Frontend ist nun erreichbar unter

    http://localhost:3000/

## Kennenlernen der Funktionalität
#### Bitte verwenden Sie den Google Chrome Browser, da experimentelle Features verwendet werden
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
- Bearbeiten Sie die Lieferung (mobile friendly): entweder direkt mit dem grünen Button OK oder indem Sie in die
  betreffende Zeile klicken und im Modal Ok, Fehlt, teilweise geliefert oder Fehler wählen.
- Klicken Sie auf die Kamera und lesen Sie den Barcode der Datei barcodeSchokolade.gif ein.
- Klicken sie auf das Mikrofon und sagen Sie "Bio". Das Wort erscheint im Suchfeld. Ggf. versuchen Sie es bitte
  mehrfach.
- Im Reiter Bearbeitet stehen die bearbeiteten Positionen. Sie können diese zurück verschieben in den Reiter
  Meldung, indem Sie auf die Zeile klicken und "Bearbeiten" klicken.
- Sind alle Positionen bearbeitet, erscheint ein Button "Lieferung als bearbeitet bestätigen". Bestätigen Sie dies.
- Loggen Sie sich ein als User "Immer Fair"
- Laden Sie im Menüpunkt Lieferungen die csv-Datei herunter
- Laden Sie im Menüpunkt csv hochladen die bewusst fehlerhafte Datei wareeingang_mitPreisFehler.csv hoch.
- Im Menüpunkt Lieferungen können Sie diese fehlerhafte Lieferung prüfen, die Fehlermeldungen als csv herunterladen oder
  die Lieferung löschen

### Tests ausführen

Frontend: im Ordner /frontend

    npm install
    npm run test (ggf. zweimal)

Backend: IntelliJ oder Unit Tests bzw. Integration Tests mittels

    mvn test
    mvn verify
### Deployment mit Docker
Erzeugen Sie ein jar im Unterverzeichnis /target mittels

    mvn clean install 

Führen Sie im root Verzeichnis aus

    docker-compose up -d

Prüfen Sie den Start des Frontends und Backends ggf. mit

    docker-compose logs -f

Sie verlassen die Anzeige mit STRG +c.

Sollte der postgres container erst nach dem Start des backends erreichbar sein und Sie eine Fehlermeldung erhalten,
führen Sie erneut aus

     docker-compose up -d

Gehen Sie in den database container mit

    docker exec -it database bash  

Geben Sie dort ein (bei POSTGRES_ADMIN_NAME = admin)

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

### Continuous Integration: Setup
Dies ist eine Anleitung für Docker unter Windows 10. Mit macOS oder Linux
verläuft die Installation ähnlich.

Starten sie den Jenkins Container und den Git Server Container mittels

    docker-compose -f docker-compose_jenkins.yml up
Bitte beachten Sie die Hardware-Requirements:
https://docs.gitlab.com/ee/install/requirements.html#hardware-requirements

Sie erreichen Jenkins im Browser unter

    http://localhost:8081/
Hinterlegen sie in C:\Windows\System32\drivers\etc\hosts

     127.0.0.1 gitlab.example.com
Damit erreichen sie den Git Server im Browser unter

     gitlab.example.com:8090
#### Git Server einrichten und pushen des Codes
Erstellen Sie im Git Server eine neue Gruppe "jenkins"
und ein neues Projekt "wareneingang".

Erstellen Sie eine Userin "besserewelt" mit Passwort "12345678".

Fügen Sie dem Projekt wareneingang die Userin besserewelt mit der
Rolle Maintainer hinzu.

Führen Sie in einem Zielverzeichnis für Ihr Projekt aus

     git clone http://besserewelt:12345678@gitlab.example.com:8090/jenkins/wareneingang.git
Kopieren Sie den Inhalt des Repositories https://github.com/Richards275/wareneingang
in dieses Verzeichnis und führen Sie dort aus

     git status
     git add .
     git status
     git commit -m "Add files"
     git push origin master
Prüfen Sie im Repository des Git Servers, dass der Code vorliegt.

#### Jenkins Job einrichten
Installieren Sie die Plugins "Strict Crumb Issuer", "Role-based Authorization Strategy",
"Docker" und "Docker Pipeline".

Wählen Sie unter Manage Jenkins -> Configure Global Security -> CSRF Protection
den "Strict Crumb Issuer" und entfernen Sie unter Advanced das Häkchen
"Check the session ID."

Erzeugen Sie die Userin "jenkins" mit Passwort "1234".

Aktivieren Sie das zweite Plugin: Wählen Sie unter "configure global security" aus
"role-based strategy".

Erstellen Sie eine Rolle "trigger-jobs" mit den Berechtigungen Overall Read, Job read, Job Build

Geben Sie der Userin jenkins die Rolle trigger-jobs.

Hinterlegen Sie unter global credentials die Git Server credentials:
besserewelt, 12345678, id: git_user

Legen Sie einen neuen Job "wareneingang" an und wählen Sie dabei "Pipeline".

Kopieren Sie unter "Pipeline" nach Auswahl von "Pipeline Script" den Inhalt der Datei "Jenkinsfile" ein.

#### Git Server custom hook einrichten
Bei jedem push des Codes soll ein Build angestoßen werden. Führen Sie aus

    docker exec -it git-server bash
    cd /var/opt/gitlab/git-data/repositories/
    grep -R wareneingang
Wechseln Sie in das angegebene .git-Verzeichnis, der Befehl lautet in etwa

    cd @hashed/d4/73/d4735e3a265e16eee03f59718b9b5d03019c07d8b11111111111.git/

Testen Sie die Verbindung mit

    curl -u "jenkins:1234" -s 'http://jenkins:8080/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)'

Der Crumb wird Ihnen angezeigt. Testen Sie das Triggern des Jobs mittels

    crumb=$(curl -u "jenkins:1234" -s 'http://jenkins:8080/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,":",//crumb)')
    curl -u "jenkins:1234" -H "$crumb" -X POST http://jenkins:8080/job/wareneingang/build?delay=0sec
Prüfen Sie, ob automatisch in Jenkins der Job "wareneingang" losläuft.

Hinterlegen Sie das auszuführende Script mittels

    mkdir custom_hooks
    cd custom_hooks
    vi post-receive
    paste Inhalt der Datei post-receive
    chmod +x post-receive
    cd ..
    chown git:git custom_hooks/ -R

Ergänzen Sie in der README.md Datei eine Zeile und führen Sie aus

    git status
    git add .
    git commit -m "Test git hook trigger"
    git push origin master

Der Jenkins Job läuft an.

Für einen Einsatz in Production müssen selbstverständlich weitere stages in der pipeline ergänzt werden. 