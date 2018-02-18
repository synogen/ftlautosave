# ftlautosave
Autosave program for **FTL Faster Than Light - Advanced Edition**, simply backs the save files up as they are updated.

## Description

Faster Than Light continuously changes the profile and the save-file of the player (ae_prof.sav and continue.sav) while playing, but does not allow keeping saves / reverting back to an old save. This program circumvents that by monitoring the two save-files (profile and game progress) and creating a backup copy every time one of them changes.
This allows you to restore a previous save-state and continue from there.

## Usage

ftlautosave requires Java 8 or higher to run. If run directly in FTL's game folder it should detect the executable to run FTL automatically, otherwise that has to be set manually. Check the [releases](https://github.com/synogen/ftlautosave/releases) if you need a pre-compiled jar, further instructions on how to run the program are attached in a readme in the release-zip.

Once the program is running it automatically monitors the default save directory of FTL on Windows (\Users\\<user\>\My Documents\My Games\FasterThanLight). The UI shows all snapshots that have been created as timestamps, sorted by time in descending order. Right now this list is not automatically updated, you have to manually refresh it using the appropriate button. Restore a snapshot by selecting it, then clicking the appropriate button below.

![Preview](/images/preview.png)

FTL's save folder and game run path can be configured directly in the UI. Status indicators below show if the save file path and the run path have been configured correctly.
More advanced settings can be found in ftlautosave.json which is created on first startup.

## Issues

ftlautosave has only been tested on Windows. Given that the save folder and FTL run path are set correctly it should work on other platforms. If you happen to test it on another platform and it doesn't work you can create an issue describing what went wrong and attach the ftlautosave.log if you like.

## Credits

Program icon used created by [paomedia](https://www.iconfinder.com/paomedia) under [CC BY 3.0](https://creativecommons.org/licenses/by/3.0/)