# Personal Physical Tracker 🏃‍♂️📍

An interactive native Android application designed to track, manage, and analyze daily physical activities. Built entirely in Java, this app allows users to log different activities (like walking, driving, studying), monitor their step count, and analyze their habits through detailed charts. 

A core feature of the app is its deep integration with Google Maps and the Geofencing API, allowing users to track activities bound to specific geographic areas (e.g., a gym or a park).

## 🚀 Key Features

* **Activity Tracking & Step Counter:** Start and stop activities with a built-in timer. Automatically counts steps using the Android `TYPE_STEP_COUNTER` hardware sensor when the "Walking" activity is selected.
* **Dynamic Activity Management:** Users can add custom activities or remove existing ones, with data persistently saved using `SharedPreferences`.
* **Google Maps & Geofencing:** Users can drop pins on a map to create 100-meter radius Geofences. The app uses a `BroadcastReceiver` to trigger push notifications when the user enters, dwells, or exits a saved zone.
* **Advanced Data Visualization:** Includes an interactive Calendar View to filter activities by date. Generates dynamic Bar Charts and Pie Charts to visualize daily activity duration and percentages.
* **Geofence-Specific Analytics:** Provides isolated statistical charts for activities performed *only* within a specific Geofence.
* **Background Notifications:** Employs `PeriodicNotificationWorker` to send timely reminders to the user to log their activities.

## 🛠️ Tech Stack & Architecture

* **Language:** Java 
* **Architecture:** MVVM (Model-View-ViewModel) using `ViewModel` and `LiveData` for a reactive, lifecycle-aware UI.
* **Local Storage:** SQLite/Room Database (`AppDatabase` for activities, `GeoDatabase` for geofences).
* **APIs & Services:** Google Maps API, Google Location & Geofencing API.

## 🎓 Academic Context
This project was developed for the "Mobile Applications Laboratory" course at the University of Bologna (Informatica per il management).
