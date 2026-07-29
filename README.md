<img align="left" width="80" height="80"
src=".github/repo_icon.png" alt="App icon">

# Metroboard [![Metroboard CI](https://github.com/Cyanexani/metrokeyboard/actions/workflows/android.yml/badge.svg?event=push)](https://github.com/Cyanexani/metrokeyboard/actions/workflows/android.yml)

**Metroboard** is the official default keyboard included in **Metro OS**, built as a free, open-source input method for Android 8.0+ devices. Designed to complement Metro OS with sleek Metro-inspired aesthetics, modern user-friendly features, and deep customization, Metroboard operates with complete respect for your privacy. Currently in beta state.

<table>
<tr>
<th style="text-align: center; width: 50%">
<h3>Stable <a href="https://github.com/Cyanexani/metrokeyboard/releases/latest"><img alt="Latest stable release" src="https://img.shields.io/github/v/release/Cyanexani/metrokeyboard?sort=semver&display_name=tag&color=28a745"></a></h3>
</th>
<th style="text-align: center; width: 50%">
<h3>Preview <a href="https://github.com/Cyanexani/metrokeyboard/releases"><img alt="Latest preview release" src="https://img.shields.io/github/v/release/Cyanexani/metrokeyboard?include_prereleases&sort=semver&display_name=tag&color=fd7e14"></a></h3>
</th>
</tr>
<tr>
<td style="vertical-align: top">
<p><i>Major versions only</i><br><br>Updates are more polished, new features are matured and tested through to ensure a stable experience.</p>
</td>
<td style="vertical-align: top">
<p><i>Major + Alpha/Beta/Rc versions</i><br><br>Updates contain new features that may not be fully matured yet and bugs are more likely to occur. Allows you to give early feedback.</p>
</td>
</tr>
<tr>
<td style="vertical-align: top">
<p>
<a href="https://apt.izzysoft.de/fdroid/index/apk/dev.patrickgold.metroboard"><img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" height="64" alt="IzzySoft repo badge"></a>
<a href="https://f-droid.org/packages/dev.patrickgold.metroboard"><img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" height="64" alt="F-Droid badge"></a>
</p>
<p>

**Google Play**: Join the Metroboard Test Group, then visit the testing page. Once joined and installed, updates will be delivered like for any other app. ([Store entry](https://play.google.com/store/apps/details?id=dev.patrickgold.metroboard))

</p>
<p>

**Obtainium**: Auto-import stable config

</p>
<p>

**Manual**: Download and install the APK from the release page.

</p>
</td>
<td style="vertical-align: top">
<p><a href="https://apt.izzysoft.de/fdroid/index/apk/dev.patrickgold.metroboard.beta"><img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" height="64" alt="IzzySoft repo badge"></a></p>
<p>

**Google Play**: Join the Metroboard Test Group, then visit the preview testing page. Once joined and installed, updates will be delivered like for any other app. ([Store entry](https://play.google.com/store/apps/details?id=dev.patrickgold.metroboard.beta))

</p>
<p>

**Obtainium**: Auto-import preview config

</p>
<p>

**Manual**: Download and install the APK from the release page.

</p>
</td>
</tr>
</table>

Beginning with v0.7 Metroboard will enter the public beta on Google Play.

## Highlighted features
- Integrated clipboard manager / history
- Advanced theming support and customization
- Integrated extension support (still evolving)
- Emoji keyboard / history / suggestions

Feature roadmap: See [ROADMAP.md](ROADMAP.md)

## Contributing
Want to contribute to Metroboard? That's great to hear! There are lots of
different ways to help out, please see the [contribution guidelines](CONTRIBUTING.md) for more info.

## List of permissions Metroboard requests
Please refer to the documentation to get more information on this topic.

## APK signing certificate hashes

The package names and SHA-256 hashes of the signature certificate are listed below, so you can verify both Metroboard variants with apksigner by using `apksigner verify --print-certs Metroboard-<version>-<track>.apk` when you download the APK.
If you have [AppVerifier](https://github.com/soupslurpr/AppVerifier) installed, you can alternatively copy both the package name and the hash of the corresponding track and share them to AppVerifier.

##### Stable track:

dev.patrickgold.metroboard<br>
0B:80:71:64:50:8E:AF:EB:1F:BB:81:5B:E7:A2:3C:77:FE:68:9D:94:B1:43:75:C9:9B:DA:A9:B6:57:7F:D6:D6

##### Preview track:

dev.patrickgold.metroboard.beta<br>
0B:80:71:64:50:8E:AF:EB:1F:BB:81:5B:E7:A2:3C:77:FE:68:9D:94:B1:43:75:C9:9B:DA:A9:B6:57:7F:D6:D6


## Used libraries, components and icons
* [AndroidX libraries](https://github.com/androidx/androidx) by
  [Android Jetpack](https://github.com/androidx)
* [AboutLibraries](https://github.com/mikepenz/AboutLibraries) by
  [mikepenz](https://github.com/mikepenz)
* [Google Material icons](https://github.com/google/material-design-icons) by
  [Google](https://github.com/google)
* [JetPref preference library](https://github.com/patrickgold/jetpref) by
  [patrickgold](https://github.com/patrickgold)
* [KotlinX coroutines library](https://github.com/Kotlin/kotlinx.coroutines) by
  [Kotlin](https://github.com/Kotlin)
* [KotlinX serialization library](https://github.com/Kotlin/kotlinx.serialization) by
  [Kotlin](https://github.com/Kotlin)

Many thanks to [Nikolay Anzarov](https://www.behance.net/nikolayanzarov) ([@BloodRaven0](https://github.com/BloodRaven0)) for designing and providing the main app icons to this project!

## License
```
Copyright 2020-2026 The Metroboard Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

Thanks to [The Metroboard Contributors](https://github.com/Cyanexani/metrokeyboard/graphs/contributors) for making this project possible!

