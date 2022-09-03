# sutom-slackbot - Bot slack pour générer des podiums SUTOM

[![Build Status](https://github.com/bmaparpaing/sutom-slackbot/actions/workflows/main.yml/badge.svg)](https://github.com/bmaparpaing/sutom-slackbot/actions/workflows/main.yml)

**[SUTOM](https://sutom.nocle.fr/)** est un jeu populaire français basé sur Wordle et Motus. Sa fonction "Partager"
permet de copier dans le presse-papier un diagramme de la partie du jour sous forme d'émojis pour le partager via
divers réseaux sociaux, notamment [Slack](https://slack.com/). Cet outil permet de se connecter à l'API Slack et de
récupérer les partages de parties SUTOM dans le but de générer un classement des joueurs par jour ou par semaine.

## 🚀 Installation et exécution

L'application nécessite un accès à l'[API Slack](https://api.slack.com/).
Générez un jeton d'accès en [ajoutant une app](https://api.slack.com/apps) à votre espace slack.
Notez l'ID du canal auquel l'application doit se connecter.

Configurez l'application avec le jeton d'accès et l'ID canal soit dans le fichier `application.properties`, soit avec
les variables d'environnement `SLACK_TOKEN` et `SLACK_CHANNEL`.

L'application se lance avec les commandes suivantes :

```shell
# Génère le classement pour aujourd'hui
mvn spring-boot:run -Dspring-boot.run.arguments="jour"

# Génère le classement pour la semaine, c'est-à-dire depuis le lundi de la semaine en cours jusqu'à aujourd'hui
mvn spring-boot:run -Dspring-boot.run.arguments="semaine"
```

Le classement est envoyé dans le même canal que celui qui est configuré.

## 🧮 Calcul du score

* Un joueur gagne s'il trouve le mot du jour en moins de coups qu'un autre.
* En cas d'égalité, il gagne s'il trouve avec moins de lettres rouges (lettres bien placées).
* En cas d'égalité, il gagne s'il trouve avec moins de lettres jaunes (lettres mal placées).
* En cas d'égalité, le gagnant est celui qui a partagé son score en premier.