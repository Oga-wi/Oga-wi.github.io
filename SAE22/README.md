# SAE22 — Synthèse et Effets Audio en MATLAB

Projet de traitement du signal audio réalisé dans le cadre de la SAE22 (IUT Nancy-Brabois, BUT Réseaux & Télécommunications).  
Il regroupe un synthétiseur de sons de guitare par l'algorithme de **Karplus-Strong** et une collection d'**effets audio** appliqués en traitement du signal.

---

## Structure du projet

```
SAE22/
├── Karplus.m              # Synthèse Karplus-Strong (note seule)
├── KarplusCombine.m       # Synthèse de deux notes en accord (simultané)
├── KarplusSuite.m         # Synthèse de deux notes en séquence (l'une après l'autre)
├── effet_bitcrusher.m     # Réduction de la résolution numérique
├── effet_chorus.m         # Effet chorus (doublage légèrement décalé)
├── effet_delay.m          # Écho simple avec atténuation
├── effet_distortion.m     # Distorsion harmonique (saturation)
├── effet_flange.m         # Flanger (retard modulé en basse fréquence)
├── effet_multiplechoes.m  # Échos multiples avec atténuations progressives
└── effet_reverb.m         # Réverbération par filtre FIR
```

---

## Synthèse Karplus-Strong

L'algorithme de Karplus-Strong simule la vibration d'une corde pincée par filtrage récursif d'un bruit blanc.

### `Karplus(F0, a, repetition)`

Génère un signal audio imitant une note de guitare à la fréquence `F0`.

| Paramètre   | Description                                      |
|-------------|--------------------------------------------------|
| `F0`        | Fréquence fondamentale de la note (Hz)           |
| `a`         | Coefficient de lissage du filtre (proche de 1)   |
| `repetition`| Nombre de répétitions de la note                 |

**Retourne** : `s_total` — vecteur audio (Fe = 44 100 Hz, durée = 0.25 s × répétitions)

### `KarplusCombine(F1, F2)`

Joue deux notes **simultanément** (accord) en additionnant les deux signaux.

### `KarplusSuite(F1, F2)`

Joue deux notes **l'une après l'autre** (mélodie) en concaténant les signaux.

**Exemple d'utilisation :**
```matlab
% Note La (440 Hz), coefficient 0.99, 3 répétitions
s = Karplus(440, 0.99, 3);
soundsc(s, 44100);

% Accord La + Mi
KarplusCombine(440, 329.63);

% Suite Do + Sol
KarplusSuite(261.63, 392);
```

---

## Effets Audio

Tous les effets prennent en entrée un vecteur signal `X` et retournent un signal traité `Y`.

### `effet_bitcrusher(X, bits)`

Réduit la résolution numérique du signal pour un effet lo-fi / rétro.

| Paramètre | Description                          |
|-----------|--------------------------------------|
| `X`       | Signal d'entrée                      |
| `bits`    | Résolution cible (ex: 8, 4, 2 bits)  |

---

### `effet_chorus(X, FS, coeff)`

Ajoute une copie légèrement décalée du signal pour un effet d'épaississement sonore.

| Paramètre | Description                          |
|-----------|--------------------------------------|
| `X`       | Signal d'entrée                      |
| `FS`      | Fréquence d'échantillonnage (Hz)     |
| `coeff`   | Intensité du chorus (entre 0 et 1)   |

---

### `effet_delay(X, FS, att, d)`

Génère un écho unique avec atténuation.

| Paramètre | Description                              |
|-----------|------------------------------------------|
| `X`       | Signal d'entrée                          |
| `FS`      | Fréquence d'échantillonnage (Hz)         |
| `att`     | Atténuation de l'écho (entre 0 et 1)     |
| `d`       | Décalage de l'écho en millisecondes      |

---

### `effet_distortion(X, FS, A)`

Applique une saturation harmonique via une fonction arctan.

| Paramètre | Description                              |
|-----------|------------------------------------------|
| `X`       | Signal d'entrée                          |
| `FS`      | Fréquence d'échantillonnage (Hz)         |
| `A`       | Intensité de la distorsion (≥ 0)         |

---

### `effet_flange(X, FS, puissance)`

Ajoute un retard court modulé en basse fréquence, créant un effet de « jet d'avion ».

| Paramètre  | Description                              |
|------------|------------------------------------------|
| `X`        | Signal d'entrée                          |
| `FS`       | Fréquence d'échantillonnage (Hz)         |
| `puissance`| Amplitude du décalage et période de modulation |

---

### `effet_multiplechoes(X, FS, att, d)`

Génère quatre échos successifs avec atténuations décroissantes.

| Paramètre | Description                              |
|-----------|------------------------------------------|
| `X`       | Signal d'entrée                          |
| `FS`      | Fréquence d'échantillonnage (Hz)         |
| `att`     | Atténuation du premier écho              |
| `d`       | Espacement entre les échos (ms)          |

---

### `effet_reverb(X, FS, temps, attenuation)`

Simule une réverbération de salle via un filtre FIR à réponse impulsionnelle longue.

| Paramètre    | Description                              |
|--------------|------------------------------------------|
| `X`          | Signal d'entrée                          |
| `FS`         | Fréquence d'échantillonnage (Hz)         |
| `temps`      | Durée de réverbération en millisecondes  |
| `attenuation`| Niveau d'atténuation des réflexions      |

---

## Exemple complet

```matlab
Fe = 44100;

% Génération d'une note
s = Karplus(220, 0.99, 2);

% Application d'une réverbération
s_reverb = effet_reverb(s', Fe, 80, 0.4);

% Application d'un delay
s_delay = effet_delay(s_reverb, Fe, 0.5, 300);

soundsc(s_delay, Fe);
```

---

## Environnement

- **MATLAB** R2022b ou supérieur (testé sous licence IUT)
- Fréquence d'échantillonnage standard : **44 100 Hz**
- Aucune toolbox externe requise

---

## Crédits

Les fonctions d'effets audio (`effet_flange`, `effet_chorus`, `effet_delay`, `effet_multiplechoes`, `effet_distortion`, `effet_reverb`) sont adaptées du projet **Guitar Effects Pedal** publié sur MATLAB Central File Exchange :

> F. Bonnel, S. Crase, A. Desmazures, J. Marcq (2006).  
> *Guitar Effects Pedal — Pédale d'effet pour guitare*  
> MATLAB Central File Exchange.  
> https://www.mathworks.com/matlabcentral/fileexchange/11582-guitar-effects-pedal-pedale-d-effet-pour-guitare

Les fonctions de synthèse Karplus-Strong (`Karplus`, `KarplusCombine`, `KarplusSuite`) ont été développées dans le cadre de la SAE22.

---

## Auteur

**Gabriel Babini** — Ogami / [Oga-wi](https://github.com/Oga-wi)  
