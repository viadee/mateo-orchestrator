# Mateo Orchestrator

Repository für den Mateo-Orchestrator

Der Mateo-Orchestrator ist eine eigenständige Anwendung, die es ermöglicht mateo Instanzen zu orchestrieren.

## Funktionsweise
Der Mateo-Orchestrator besitzt eine Liste von Mateo-Instanzen. 
Per Rest-Api können Testskripte zur Ausführung an den Orchestrator übergeben werden.
Es wird eine Id zurückgegeben mit welcher der Status des Jobs abgefragt werden kann.

### Job
Ein [Job](./rest_doku.md#jobdto) besteht aus: 
- Einer Uuid
- Dem auszuführenden Testskriptnamen (inkl. Pfad)
- Einer Map von Variablen für das Testskript (Eingabe und Ausgabe)
- Dem JobStatus (`In Warteschlange`, `Wird ausgeführt`, `Beendet`)
- Ergebnis von mateo ([Report](./rest_doku.md#reportdto))

#### Warnung
_Die Jobs werden (aktuell) nicht persistiert und sind nach Beenden der Anwendung gelöscht._

## Voraussetzungen und unterstützte Umgebungen
- Mindestens eine laufende Mateo-Instanz
- Jede Mateo-Instanz muss dieselben Testskripte enthalten

## Konfiguration
Der Mateo-Orchestrator ist über die Datei `application.yml` konfigurierbar.

Präfix | Eigenschaften | Beschreibung
-------- | -------- | --------
de.viadee.mateo.orchestrator.mateo-api | urls | Die URLs (kommasepariert) zu den Mateo-Instanzen (z.B. http://localhost:8123, http://localhost:8124)

## Nutzung
Zunächst wird ein Job per [POST](./rest_doku.md#post) (und den entsprechenden [Parametern](./rest_doku.md#parameters-1)) mit dem Endpunkt `/api/jobEntity/start` gestartet.
Als [Antwort](./rest_doku.md#responses-2) wird die Uuid des erstellten Jobs zurückgegeben.
Mit dieser Uuid kann per [GET](./rest_doku.md#apijob) über den Endpunkt `/api/jobEntity{uuid}` der Status abgefragt werden.
Für weitere Informationen, siehe [Rest Dokumentation](./rest_doku.md).

_**Wichtig:** Wenn Variablen übergeben werden sollen, muss die Map sowohl die **Inputvariablen** (inkl. des Variablenwerts) als auch die **Ergebnisvariablen** (ohne Wert möglich) enthalten.
Alle angegebenen Variablen werden inkl. Wert in den Storage geschrieben und nach der Ausführung des Skripts ausgelesen und zurückgegeben._

### Beispiel
POST: <br>
[http://localhost:8083/api/jobEntity/start?scriptFile=/opt/mateo/Scripts/Beispiel Chrome/Beispiel Chrome.xlsm]() 

Body:<br>
`{
  "scriptVariable": "Hallo",
  "result" : ""
}`


Mit der zurückgegebenen Id kann nun der Status abgefragt werden:<br>
GET: http://localhost:8083/api/jobEntity?uuid=4c74d8c4-1c0c-4e43-8725-32e867e76b23
