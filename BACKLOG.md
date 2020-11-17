# Backlog (projet visulog)

### Description du projet

Outils permettant d'analyser et visualiser les logs des dépôts gits.  
À l'aide d'un outil en ligne de commandes, il permet d'obtenir une page HTML contenant tous les résultats des informations demandés.

### Membres du projet

- **CLIPAL** Moïse
- **JAUROYON** Maxime
- **MOHAMED CASSIM** Tasnim
- **PAYET** Jylan
- **SOMON** Bastian
- **ALLANO** Yoann
- **KINDEL** Hugo

### Dates majeurs

- Création du projet le **15 septembre**.
- Deadline final le **6 janvier**.

### Librairies à utiliser

- JGit  
Introduit un interface Java pour manipuler les dépôts git.
- HtmlFlow  
Génère des documents HTML bien typées (donc correct par rapport au système de validation W3C).
- CanvasJS  
Éditer des visualisations numériques (courbes, histogrammes, etc.).

## Réunion de la semaine 3

Révision des semaines 1 et 2

### Travail effectué

- Analyse du code d'origine (les fichiers sont maintenant tous documentés pour plus de précisions):
	- analyser - @hugokindel:
		- Analyse le contenu des logs git à l'aide de config et gitrawdata, et permet d'obtenir le résultat.
	- cli - @jauroyon:
		- Contient le *main* principale, possède le système d'options et exécute l'analyser à l'aide de ceux-ci.
	- config - @clipal:
		- Possède des classes de configurations pour aider l'analyser et cli.
	- gitrawdata - @somon:
		- Possède des classes de données correspondant aux éléments de git pour pouvoir les traiter.
- Gestion d'un bug empêchant la compilation (problème d'Unicode)
- Issues réalisés (merge requests correspondantes fermés):
	- #2 - @hugokindel
	- #9 - @somon
	- #14 - @clipal
	- #5 - @jauroyon

### Rétrospective des problèmes rencontrés

- Soucis d'organisation.
- Avancement du projet sans mettre tous le monde au même niveau.

### Planification des semaines à venir

- Finir les issues assignée à chacun.
- Faire #8 à plusieurs.

## Réunion de la semaine 5

Révision des semaines 3 et 4

### Travail effectué

- Issues réalisés (merge requests correspondantes fermés):
	- #4 - @hugokindel
	- #17 - @allano
	- #3 - @hugokindel
	- #7 - @jauroyon
	- #10 - @somon
	- #11 - @somon
	- #12 - @mohamedc
- Issues en cours de réalisation:
	- #16 - @hugokindel @jauroyon

### Rétrospective des problèmes rencontrés

- Soucis d'organisation (à cause des changements de la situation sanitaire).
- Certaines issues du projet d'origine déjà faite ou mal indiqués (à vérifier lors de la planification).

### Planification des semaines à venir

- Finir les issues du sprint 1 restantes puis aller sur le groupe s'occupant d'ajouter des nouvelles fonctionnalités. @hugokindel @jauroyon
- Commencer le projet webgen (générer une page HTML, utiliser HtmlFlow et CanvasJS, ...). @clipal @somon @allano @payet
- Ajouter des nouvelles fonctionnalités (classe branch dans gitrawdata, nouvelles options, ...). @mohamedc

## Réunion de la semaine 7

Révision des semaines 5 et 6

### Travail effectué

- Issues réalisés (merge requests correspondantes fermés):
	- #16 - @jauroyon @hugokindel
	- #21 - @jauroyon
	- #15 - @clipal @hugokindel
	- #13 - @payet
	- #8 - @allano
	- #22 - @mohamedc
	- #6 - @mohamedc
- Issues en cours de réalisation:
	- #18 - @clipal @payet @allano @somon

### Rétrospective des problèmes rencontrés

- Soucis d'emploi du temps (dû aux demplois de certains membres du groupe et au reconfinement).

### Planification des semaines à venir

- Continuer le travail actuel avec les mêmes répartitions d'équipes (faire webgen, ajouter des fonctionnalités/corriger le code).
- Bien utiliser les librairies pour le développement de webgen !

## Réunion de la semaine 9

Révision des semaines 7 et 8

### Travail effectué

- Issues réalisés (merge requests correspondantes fermés):
	- #24 - @somon
	- #30 - @mohamedc
	- #31 - @hugokindel
	- #23 - @somon
	- #26 - @payet
	- #32 - @somon @payet @clipal @allano
	- #27 - @somon @payet @clipal @allano
	- #18 - @somon @payet @clipal @allano
	- #33 - @somon
- Issues en cours de réalisation:
	- #28 - @hugokindel @mohamedc
	- #29 - @jauroyon
- À réaliser:
	- #25 - @jauroyon @mohamedc

### Rétrospective des problèmes rencontrés

- Répartition inégale du nombre de commit par personne, du à des soucis d'organisations, d'emploi du temps, etc. *doit être corrigé!*

### Planification des semaines à venir

- Implémenter JGit.
- Ajouter et fonctionnalités répondants aux user-stories (majoritairement des plugins et options de commandes) maintenant que la majorité du logiciel est bien intégré.