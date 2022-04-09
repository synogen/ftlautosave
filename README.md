# [ftlautosave](https://github.com/synogen/ftlautosave)

Autosave program for **FTL Faster Than Light - Advanced Edition**, simply backs the save files up as they are updated.

## Description

Faster Than Light continuously changes the profile and the save-file of the player (ae_prof.sav and continue.sav) while playing, but does not allow keeping saves / reverting back to an old save.
This program circumvents that by monitoring the two save-files (profile and game progress) and creating a backup copy every time one of them changes.
This allows you to restore a previous save-state and continue from there.

## Run

ftlautosave requires [Java Runtime Environment 8](https://www.oracle.com/java/technologies/downloads/) or higher to run.
You can either start ftlautosave.jar directly or use the supplied ftlautosave.cmd file (for Windows, also works for Mac and Unix using `sh ftlautosave.cmd`).
If run directly in FTL's game folder it should detect the executable to run FTL automatically, otherwise that has to be set manually.

## Usage

Once the program is running it automatically monitors the default save directory of FTL (on Windows: ~\My Documents\My Games\FasterThanLight, Mac: ~/Library/Application Support/FasterThanLight, Unix: ~/.local/share/FasterThanLight).
The UI shows all save snapshots that have been created with some basic info about the save and a timestamp, sorted by time in descending order.
Save snapshots of key events can be marked, turning them yellow, so they can be more easily found back.
Marking already marked saves removes their mark, and marks are lost after quitting.
Restore a snapshot by selecting it, then clicking the appropriate button below.
Usually you won't need to quit FTL to restore a snapshot, just go to the game's main menu, switch to ftlautosave, restore the snapshot you'd like then switch back to FTL and continue.

![Preview](/images/preview.png)

FTL's save folder and game run path can be configured directly in the UI.
Status indicators below show if the save file path and the run path have been configured correctly.
To streamline playing FTL with ftlautosave you can choose to start FTL automatically when ftlautosave is launched.
That way you only need to start one program and you can be sure your saves are being monitored.
The snapshot list is updated automatically by default.
There is also a default limit of 500 snapshots after which the program will start deleting the oldest snapshots, this is to prevent getting too many old save files in the save directory and the program getting sluggish when refreshing all of them.
You can of course override the default configuration if you wish to retain all save files or if you want to manage the old save files by yourself. 

## Download

Check the [releases](https://github.com/synogen/ftlautosave/releases) if you need a pre-compiled jar.

## Issues

ftlautosave has been tested on Windows and Mac. Given that the save folder and FTL run path are set correctly it should work on other platforms. If you happen to test it on another platform and it doesn't work you can create an issue describing what went wrong and attach the ftlautosave.log if you like.

## Credits

Program icon used created by [paomedia](https://www.iconfinder.com/paomedia) under [CC BY 3.0](https://creativecommons.org/licenses/by/3.0/)
