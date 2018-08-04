# OutdoorsmanApp
NAME: OutdoorsManApp

INSTALLATION: Download the project from the github link, open in Android Studio and choose target to build and install for.

USAGE: 
- Sign in using email and password or using a google account
- Add form to add records to the database
- View your previous records in a listview
- Clicking a record takes you to the map view with the selected records data showed on a pin that represents the location the record was recorded at
- You can also directly go to the Map fragement and see all the pins on the map
- Flicking on the record in the mapview iterates to the next record and displays that

BUG REPORT:
- If a date from a previous month is clicked, the app crashes. This is a known bug.
- Google maps must be opened before opening the app to make sure current location works right
- When the app asks for Location Permission the first time, it must be approved. If it is denied the first time, the app refuses to work until a restart
- Sometimes the google sigin method acts slow and almost gets stuck in a loop. It usually eventually works, but it may not
