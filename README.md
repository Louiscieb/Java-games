# TowerDefence

[![Java](https://img.shields.io/badge/Java-17-blue)](https://www.java.com/)
[![Gradle](https://img.shields.io/badge/Gradle-8.2-green)](https://gradle.org/)

**TowerDefence** est un jeu de type **Tower Defense** développé en **Java** avec **libGDX**.  
Le projet utilise **Tiled** pour la gestion des cartes et des niveaux.

Le module **core** contient la logique du jeu, tandis que **lwjgl3** est la plateforme bureau utilisant **LWJGL3**.

---

## Fonctionnalités

- Défense de base contre des vagues d’ennemis.
- Gestion de tours et projectiles.
- Cartes et niveaux créés avec **Tiled**.
- Interface graphique simple et responsive via libGDX.

---

## Structure du projet

TowerDefence/
├─ core/Audio + MVC# Logique et assets partagés

├─ lwjgl3/ # Plateforme desktop LWJGL3

├─ build/ # Dossiers de compilation

├─ assets/ # Sons, images, cartes Tiled

├─ gradlew # Wrapper Gradle

├─ build.gradle

└─ settings.gradle



## Lancer le jeu

Pour démarrer le jeu sur ordinateur, ouvrez un terminal dans le dossier racine du projet et exécutez :

- **Windows** :
```cmd
gradlew lwjgl3:run
- **Linux** :
./gradlew lwjgl3:run
