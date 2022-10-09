# sutom-slackbot - Bot slack pour générer des podiums SUTOM

[![Build Status](https://github.com/bmaparpaing/sutom-slackbot/actions/workflows/main.yml/badge.svg)](https://github.com/bmaparpaing/sutom-slackbot/actions/workflows/main.yml)
[![Gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg)](https://gitmoji.dev)
[![Version](https://img.shields.io/github/v/release/bmaparpaing/sutom-slackbot)](https://github.com/bmaparpaing/sutom-slackbot/releases)

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

Options :

```
jour [--golf|--alternate]
semaine [--golf|--alternate] [--printScore]
```

L'option `--golf` classe les joueurs selon un autre algorithme (voir [Mode Golf](#-mode-golf)).

L'option `--alternate` alterne la génération du classement entre le mode classique et le mode Golf d'une semaine à
l'autre. Il n'est pas nécessaire de fournir l'option `--golf` avec cet argument.

L'option `--printScore` envoie également le détail du score pour le podium semaine juste en dessous du classement.

## 🧮 Calcul du score

### 🏆 Mode classique

#### 📅 Classement jour

* Un joueur gagne s'il trouve le mot du jour en moins de coups qu'un autre.
* En cas d'égalité, il gagne s'il trouve avec moins de lettres rouges (lettres bien placées).
* En cas d'égalité, il gagne s'il trouve avec moins de lettres jaunes (lettres mal placées).
* En cas d'égalité, le gagnant est celui qui a partagé son score en premier.
* Les joueurs ayant perdu au SUTOM sont considérés derniers. Pour les départager :
  * Le joueur gagne s'il trouve avec le plus de lettres rouges.
  * En cas d'égalité, il gagne s'il trouve avec le plus de lettres jaunes.
  * En cas d'égalité, le gagnant est celui qui a partagé son score en premier.

#### 🗓️ Classement semaine

* Un joueur gagne autant de points que son placement dans le classement jour :
  premier = 1 point, deuxième = 2 points, etc.
* Chaque jour manqué équivaut à être dernier sur ce jour.
* Celui qui a le moins de points au total sur la semaine gagne.
* En cas d'égalité, il y a égalité !

### ⛳ Mode Golf

#### 📅 Classement jour

* Un joueur gagne s'il trouve le mot du jour en moins de coups qu'un autre.
* Un joueur ayant perdu au SUTOM est considéré l'avoir fait en 7 coups.
* En cas d'égalité, il gagne s'il trouve avec le plus de lettres rouges et jaunes (bien placées et mal placées),
  sachant qu'une lettre rouge compte pour deux lettres jaunes.
* En cas d'égalité, il gagne s'il trouve avec le plus de lettres rouges (lettres bien placées).
* En cas d'égalité, il y a égalité !

#### 🗓️ Classement semaine

* La méthode de classement jour est appliquée sur tous les jours dans l'ensemble, indépendamment du podium de
  chaque jour.
* Chaque jour manqué équivaut à faire le nombre de coups du dernier de ce jour + 1. Le nombre de lettres rouges
  et jaunes sont à 0 pour ce jour.