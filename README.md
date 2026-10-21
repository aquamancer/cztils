# cztils
Quality-of-life features for runs of the Celestial Zenith in Monumenta MMO, powered by [czlib](https://github.com/aquamancer/czlib) (also made by me)

**For Fabric 1.20.4 only**

## Features
* HUD showing all party members' abilities, missing/blocked abilities, charmed tree, and grave timer
* Ability selection tooltips showing extended information, notably for curses
* Lifeline indicator hearts overlay

## Installation
### Dependencies
* [czlib](https://github.com/aquamancer/czlib)
* Fabric API
* Cloth Config API
* Mod Menu (to configure cztils, optional)

Move cztils and any missing dependencies into your Minecraft profile's `mods` folder

## Building
Put `czlib-X.X.X.jar` into the directory `libs/`

Windows:
`gradlew.bat build`

Linux: `./gradlew build`

Output .jar is located at `build/libs/`
