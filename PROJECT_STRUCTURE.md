# PROJECT_STRUCTURE.md

```
MUSE/
├── CHANGELOG.md
├── FAQ.md
├── LICENSE
├── PROJECT_STRUCTURE.md
├── README.md
├── ROADMAP.md
├── UpdateChecker.kt
├── UpdateDialog.kt
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── muse/
│           │               ├── MainActivity.kt
│           │               ├── MuseApplication.kt
│           │               ├── data/
│           │               │   ├── BackupScheduler.kt
│           │               │   ├── CopyAllWorker.kt
│           │               │   ├── CopyVerification.kt
│           │               │   ├── MuseCopyStorage.kt
│           │               │   └── TrackRepository.kt
│           │               └── ui/
│           │                   └── library/
│           │                       ├── LibraryViewModel.kt
│           │                       └── TrackAdapter.kt
│           └── res/
│               ├── drawable/
│               │   ├── ic_launcher_background.xml
│               │   ├── ic_launcher_foreground.xml
│               │   └── placeholder_artwork.xml
│               ├── layout/
│               │   ├── activity_main.xml
│               │   └── item_track.xml
│               ├── layout-land/
│               │   ├── activity_main.xml
│               │   └── item_track.xml
│               ├── mipmap-anydpi-v26/
│               │   ├── ic_launcher.xml
│               │   └── ic_launcher_round.xml
│               ├── mipmap-hdpi/
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── mipmap-mdpi/
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── mipmap-xhdpi/
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── mipmap-xxhdpi/
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── mipmap-xxxhdpi/
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── values/
│               │   ├── colors.xml
│               │   ├── colors_muse.xml
│               │   ├── strings.xml
│               │   ├── styles_muse.xml
│               │   └── themes.xml
│               ├── values-night/
│               │   └── themes.xml
│               └── xml/
│                   ├── backup_rules.xml
│                   └── data_extraction_rules.xml
├── build.gradle.kts
├── core/
│   ├── database/
│   │   ├── build.gradle.kts
│   │   ├── schemas/
│   │   │   └── com.example.muse.core.database.MuseDatabase/
│   │   │       ├── 1.json
│   │   │       └── 2.json
│   │   └── src/
│   │       └── main/
│   │           └── java/
│   │               └── com/
│   │                   └── example/
│   │                       └── muse/
│   │                           └── core/
│   │                               └── database/
│   │                                   ├── Converters.kt
│   │                                   ├── MuseDatabase.kt
│   │                                   ├── PlayEventDao.kt
│   │                                   ├── PlaylistDao.kt
│   │                                   ├── TrackDao.kt
│   │                                   └── entity/
│   │                                       ├── PlayEventEntity.kt
│   │                                       ├── PlaylistEntity.kt
│   │                                       └── TrackEntity.kt
│   ├── media/
│   │   ├── build.gradle.kts
│   │   └── src/
│   │       └── main/
│   │           └── java/
│   │               └── com/
│   │                   └── example/
│   │                       └── muse/
│   │                           └── core/
│   │                               └── media/
│   │                                   ├── MediaItemFactory.kt
│   │                                   ├── MediaStoreScanner.kt
│   │                                   ├── PlaybackEventDecider.kt
│   │                                   ├── PlaybackEventListener.kt
│   │                                   ├── PlaybackService.kt
│   │                                   ├── PlaybackStateMachine.kt
│   │                                   └── TransitionReason.kt
│   └── model/
│       ├── build.gradle.kts
│       └── src/
│           └── main/
│               └── java/
│                   └── com/
│                       └── example/
│                           └── muse/
│                               └── core/
│                                   └── model/
│                                       ├── PlayEvent.kt
│                                       ├── Playlist.kt
│                                       └── Track.kt
├── gradle/
│   ├── gradle-daemon-jvm.properties
│   ├── libs.versions.toml
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
├── screenshots/
│   ├── 1775637550bd5f.png
│   └── main_window.jpg
├── script.py
└── settings.gradle.kts
```
