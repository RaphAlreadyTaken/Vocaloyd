** Compte-rendu TP ICE **

* Pré-requis pour l'exécution du projet *
- python 3.x
- pymongo (pip3 install pymongo)
- gradle (pour la compilation du client Java)

* Travail réalisé *
Dans le cadre du TP de ICE, le travail suivant a été réalisé :
- Installation de ICE
- Ecriture d'une interface slice de gestion de discothèque (Discotheque.ice)
- Ecriture d'un client en Java (./discotheque/client)
- Ecriture d'un serveur en Python (./discotheque/server)
- Ecriture d'un makefile permettant de générer les proxy/squelette et de compiler le client Java
- Mise en place d'une base de données MongoDB hébergée sur un cluster (MongoDB Atlas) pour les musiques et leurs informations
- Implémentation du streaming (libvlc)

    Côté client (Java) :
- Implémentation d'un menu interactif (ligne de commande) permettant d'appeler les méthodes de gestion de la discothèque
- Implémentation de méthodes d'affichage des objets renvoyés
- Mise en place de gradle pour compilation
- Mise en place de l'exécution de VLC vers le stream créé côté serveur

    Côté serveur (Python) :
- Installation et utilisation de la bibliothèque pymongo pour l'interaction avec la base Mongo
- Implémentation des méthodes permettant d'interagir avec les documents de la base Mongo
- Ecriture des requêtes NoSQL associées
- Notifications côté serveur du nombre de documents sollicités pour chaque requête
- Gestion des types de retour conformément à l'interface slice
- Mise en place d'un stream envoyé sur le réseau

* Difficultés rencontrées *
- La partie relative au streaming a demandé beaucoup de temps, la documentation de VLC n'étant pas particulièrement didactique. Le streaming en lui-même n'est pas entièrement fonctionnel.
  En effet, les pistes sont lues au niveau du serveur et ne sont pas diffusées sur le réseau. Il s'agit certainement d'un problème au niveau des options "sout"
- La partie relative à la gestion d'accès multiples à un même serveur a posé des problèmes (de compréhension, principalement), et n'a pas été traitée


* Perspectives d'amélioration *
- Il est possible de facilement implémenter d'autres méthodes de recherche au niveau des musiques (gestion des genres musicaux, durée des pistes)
- On pourrait envisager, au niveau du client, une interface graphique avec recherches à critères multiples
- Une gestion plus avancée des albums est possible (transformation en classe à part, rajout d'informations spécifiques (durée, année de parution))
- Côté persistance, une base de données relationnelle pourrait être, à terme, plus pertinente qu'une base documentaire