# PvP Flag mod
Allows players to enter "PvP" mode.

Skull marks PvP flag enabled. People with PvP flag disabled (no skull) can not damage you as long as you don't have it enabled as well. In all other cases PvP works.

If you don't get how PvP works with this mod refer to table below:

| **Enemy's skull** | **Your skull** | **Can attack happen?** |
|-------------------|----------------|------------------------|
| ENABLED           | ENABLED        | YES                    |
| ENABLED           | DISABLED       | YES                    |
| DISABLED          | ENABLED        | YES                    |
| DISABLED          | DISABLED       | NO                     |

Like in Project Zomboid.

### Alternative logic
Need more classic enable disable logic? You can change it by setting new GameRule to true like so:
```
/gamerule peacefulIndomitability true
```

People with PvP disabled will not take damage at all. Refer to the table below:

| **Enemy's skull** | **Your skull** | **Can attack happen?** |
|-------------------|----------------|------------------------|
| ENABLED           | ENABLED        | YES                    |
| ENABLED           | DISABLED       | NO                     |
| DISABLED          | ENABLED        | NO                     |
| DISABLED          | DISABLED       | NO                     |



# Credits
**Skull texture:**

[![CC BY 4.0][cc-by-shield]][cc-by]

by [SkaldingDelight](https://skalding.itch.io/) 



[cc-by]: http://creativecommons.org/licenses/by/4.0/
[cc-by-image]: https://i.creativecommons.org/l/by/4.0/88x31.png
[cc-by-shield]: https://img.shields.io/badge/License-CC%20BY%204.0-lightgrey.svg
