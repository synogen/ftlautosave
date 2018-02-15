# ftlautosave
Autosave program for **FTL Faster Than Light - Advanced Edition**, simply backs the save files up as they are updated.

## Description

Faster Than Light continuously changes the profile and the save-file of the player (ae_prof.sav and continue.sav) while playing, but does not allow keeping saves / reverting back to an old save. This program circumvents that by monitoring the two save-files (profile and game progress) and creating a backup copy every time one of them changes.
This allows you to restore a previous save-state and continue from there.

## Usage

ftlautosave requires Java 8 to run. Right now it has to be run directly in FTL's game folder and it has only been tested on Windows. Check the [releases](https://github.com/synogen/ftlautosave/releases) if you need a pre-compiled jar, further instructions on how to run the program are attached in a readme in the release-zip.

Once the program is running it automatically monitors the default save directory of FTL on Windows (\Users\<user>\My Documents\My Games\FasterThanLight). The UI shows all snapshots that have been created as timestamps, sorted by time in descending order. Right now this list is not automatically updated, you have to manually refresh it using the appropriate button. Restore a snapshot by selecting it, then clicking the appropriate button below.

![Preview](/images/preview.png)

Check config.json for options in case the program does not work correctly, both the ftlSavePath and the ftlRunPath have to be properly set for it to work.

