# Sensor

Apk link https://drive.google.com/file/d/1KWn-V14oBh5ZqcOKn31piCUqMVoM85Uv/view?usp=sharing

This project is an android project created with Java language.
Feature used are: Notification service, Background service for realtime update, SQLite database for storing history of data, Bar graph gor data visualization.

Workflow of the application:
Sensor sens data in realtime. The data then storen in shared preference database. After each 5 munites interval data is stored in database and then datbase.
If usen is using the app then notification will be shown. If usen not using the app or even app is not in background then Background service will notify the user by notification.

Here the critical part is background service, because from android OREO onwards android doesn'tpermits any long running service on background. Android OS kills those services.
By using # Job Scheduler I make the app run a background service permanently and this service send notification and save values on database. 

