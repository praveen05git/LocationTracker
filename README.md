<h1 align="center">LocationTracker</h1>

<p align="center">  
 Android apps which tracks Location for every 30 seconds or when user changes location</br>
</p>
</br>
<p align="center">
  <img src="/previews/LocationTracker.png" width="30%" />
  <img src="/previews/LocationTrackerGif1.gif" width="30%" />
</p>

## Features
- Checks location every 30 seconds
- Checks location when user changes location
- Uploads the Location and Recent Uploaded time to Cloud
- Shows the number of Data uploaded Today
- Shows Today's Recent Activity
</br>

## Tech stack
- Minimum SDK level 16
- Java, MVVM Architecture based
- Used AndroidViewModel for Location Fetching and Data Uploads
- Used Firebase Realtime Database for Cloud storage and Retrieval
- Implemented MutableLiveData and Observed it for fetching required information in a Reactive manner
</br>
