# [ftlautosave](https://github.com/synogen/ftlautosave)
Autosave program for **FTL Faster Than Light - Advanced Edition**, simply backs the save files up as they are updated. Supports Multiverse.

## Description

Faster Than Light continuously changes the profile and the save-file of the player (usually ae_prof.sav and continue.sav) while playing, but does not allow keeping saves / reverting back to an old save. This program circumvents that by monitoring the two save-files (profile and game progress) and creating a backup copy every time one of them changes.
This allows you to restore a previous save-state and continue from there.

## Run

If run directly in FTL's game folder it should detect the executable to run FTL automatically, otherwise that has to be set manually.

## Usage

Once the program is running it automatically monitors the default save directory of FTL on Windows (`\Users\<user>\My Documents\My Games\FasterThanLight`). The UI shows all save snapshots that have been created with some basic info about the save and a timestamp, sorted by time in descending order. Restore a snapshot by selecting it, then clicking the appropriate button below. Usually you won't need to quit FTL to restore a snapshot, just go to the game's main menu, switch to ftlautosave, restore the snapshot you'd like then switch back to FTL and continue.

![Preview](/images/preview2.png)

FTL's save folder and game run path can be configured directly in the UI. Status indicators below show if the save file path and the run path have been configured correctly.

To streamline playing FTL with ftlautosave you can choose to start FTL automatically when ftlautosave is launched. That way you only need to start one program and you can be sure your saves are being monitored.

The snapshot list is updated automatically by default. There is also a default limit of 500 snapshots after which the program will start deleting the oldest snapshots, this is to prevent getting too many old save files in the save directory and the program getting sluggish when refreshing all of them.

Depending on what version/mod of FTL you're playing you can select the save file type to be "Advanced Edition" or "Multiverse". If it is neither of those you can still choose "Custom" and set your own file names for both the profile and the save file.

You can also override the default configuration if you wish to retain all save files or if you want to manage the old save files by yourself. This can be done by editing the `ftlautosave.json` configuration file that is generated on the first start of the program. 

## Download

Check the [releases](https://github.com/synogen/ftlautosave/releases) if you need a pre-compiled executable. Right now there is only a Windows build available. Check [the build instruction](BUILD.md) if you want to try building for other platforms. 

## Issues

ftlautosave has only been tested on Windows. If you need a binary for another platform check [the build instruction](BUILD.md). Given that the save folder and FTL run path are set correctly it should work on other platforms. If you happen to test it on another platform and it doesn't work you can create an issue describing what went wrong and attach the ftlautosave.log if you like.

## Credits

Program icon used created by [paomedia](https://www.iconfinder.com/paomedia) under [CC BY 3.0](https://creativecommons.org/licenses/by/3.0/)