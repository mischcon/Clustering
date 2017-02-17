#Projektarbeit 2
## Ziel
* Scala / Akka basiertes Framework für clustering / remoting / parallelism
* Ausführen von in Java / Scala geschriebenen Einheiten / Tasks 
* Gruppierung / Single Instance / Depedency-Management
* Failsafe
* Scaling up / out (Automatisches Starten / Stoppen von VMs; Automatisches Deployment)
* Loadbalancing
* System Monitoring (Check welche Komponenten installiert sind (VirtualBox/Vagrant) - abhängig davon Anpassung der Aufgabenvergabe)

## Wie verbindung Java Task <-> Cluster?
* Annotationen @Clustering(Group="/nodes", SingleInstance=False, ...)

## Fehler
1) Cluster Error (z.B. "Node unreachable")
    * Liste von Tasks / ausstehenden Ergebnissen im Master mitführen (not running, pending, done, error)
2) Task Fehler 
    * rerun?
    * logging + done?
    * one-for-one / all-for-one supervision strategy
3) VM Fehler / "System" Fehler
    * all-for-one
