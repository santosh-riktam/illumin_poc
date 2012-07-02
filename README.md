illumin
===========

Folder stucture
--

libs : Contains private libraries.

res : Contains application resources, such as drawable files, layout files, and string values.

src : Contains source code files (such as .java or .aidl files) go here as well.

.classpath : classpath used by java compiler

.project : eclipse configuration file

AndroidManifest.xml : The control file that describes the nature of the application and each of its components. For instance, it describes: certain qualities about the activities, services, intent receivers, and content providers; what permissions are requested; what external libraries are needed; what device features are required, what API Levels are supported or required; and others.

proguard-project.txt : proguard(Java class file shrinker, optimizer, obfuscator, and preverifier) configuration file

project.properties : Eclipse configuration file


First screen
--
layout file : / res / layout / main.xml

java class : / src / com / illumin / androidpoc / ui / Illumin_usActivity.java

 `doInBackground(String... params)` method in `UploadTask` class (Illumin_usActivity.java) contains code for performing HTTP requests and showing the progressbar.
 
 `onCreate`, `onItemClick`, `onActivityResult` contain code reponsible for showing the UI and enabling user to choose a video