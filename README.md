# Personal Physical Tracker 🏃‍♂️📍

An interactive native Android application designed to track, manage, and analyze daily physical activities. [cite_start]Built entirely in Java, this app allows users to log different activities (like walking, driving, studying), monitor their step count, and analyze their habits through detailed charts[cite: 3253, 3261, 3349]. 

[cite_start]A core feature of the app is its deep integration with Google Maps and the Geofencing API, allowing users to track activities bound to specific geographic areas (e.g., a gym or a park)[cite: 3557, 3626].

## 🚀 Key Features

* **Activity Tracking & Step Counter:** Start and stop activities with a built-in timer. [cite_start]Automatically counts steps using the Android `TYPE_STEP_COUNTER` hardware sensor when the "Walking" activity is selected[cite: 3304, 3311].
* [cite_start]**Dynamic Activity Management:** Users can add custom activities or remove existing ones, with data persistently saved using `SharedPreferences`[cite: 3354, 3359].
* [cite_start]**Google Maps & Geofencing:** Users can drop pins on a map to create 100-meter radius Geofences[cite: 3557, 3577]. [cite_start]The app uses a `BroadcastReceiver` to trigger push notifications when the user enters, dwells, or exits a saved zone[cite: 3631, 3633].
* [cite_start]**Advanced Data Visualization:** Includes an interactive Calendar View to filter activities by date[cite: 3432]. [cite_start]Generates dynamic Bar Charts and Pie Charts to visualize daily activity duration and percentages[cite: 3481].
* [cite_start]**Geofence-Specific Analytics:** Provides isolated statistical charts for activities performed *only* within a specific Geofence[cite: 3557, 3583].
* [cite_start]**Background Notifications:** Employs `PeriodicNotificationWorker` to send timely reminders to the user to log their activities[cite: 3289].

## 🛠️ Tech Stack & Architecture

* [cite_start]**Language:** Java [cite: 3261]
* [cite_start]**Architecture:** MVVM (Model-View-ViewModel) using `ViewModel` and `LiveData` for a reactive, lifecycle-aware UI[cite: 3455, 3458].
* [cite_start]**Local Storage:** SQLite/Room Database (`AppDatabase` for activities, `GeoDatabase` for geofences)[cite: 3321].
* [cite_start]**APIs & Services:** Google Maps API, Google Location & Geofencing API[cite: 3626].
* [cite_start]**Concurrency:** Background threads and `ExecutorService` for asynchronous database operations to prevent UI blocking[cite: 3318, 3330].

## 📱 Screenshots
*(Add your screenshots here by uploading them to your repository and linking them! Example: `<img src="path_to_image.png" width="200"/>`)*
* [cite_start]Home Screen & Timer [cite: 3265]
* [cite_start]Calendar & Activity History [cite: 3372]
* [cite_start]Statistics & Charts [cite: 3461]
* [cite_start]Google Maps & Geofences [cite: 3510]

## 🎓 Academic Context
[cite_start]This project was developed for the "Mobile Applications Laboratory" course at the University of Bologna (Informatica per il management)[cite: 3234, 3242, 3243].
