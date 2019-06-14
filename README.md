# Congenial Telegram
A Social Media Application with features to post text and images, share them with external apps, follow people to see their posts and in built messaging service.

## Congenial Telegram apk
[![button](https://user-images.githubusercontent.com/32104560/59460385-96c22a80-8e3c-11e9-8ac6-147ad30dade4.png)](https://drive.google.com/file/d/1wyaktLj90Xvqx7gNfJKcT6nnJwOgqaFS/view?usp=sharing)

## Libraries Used

- Firebase
- Glide
- Circular Image View

## Project Setup
After cloning the project, you need to replace the existing Firebase json file with yours. Follow the steps below to add json file to your project:
1. Create Firebase account and go to console ( it is present in upper right side of main website).
2. Click on **New Project**. A dialog box opens up. Give a suitable name and click on **Create Project**.
3. In the new window, where you have to select the platform in which you want to add Firebase, select *Android*.
4. Register app window opens. Fill the details.  
  **Package Name :** *com.example.android.congenialtelegram*    
  **App Nickname :** *Congenial Telegram*    
5. Click on Register App button. 
6. Download the json file and put it in the app directory.
7. Build the project in Android Studio. And click OK in Firebase and the project is created.
8. Next when the below window opens, go to **Sign-in Method** in **Authentication** tab.     
9. Enable *Email and Password*.
10. Setup **Realtime Database** from **Database** tab. Make sure you have the following rules in your rules section of database.

``{   
"rules": {   
  ".read": "auth != null",   
  ".write": "auth != null"   
          }     
 }``   
 
 11. Similarly, setup **Firebase Storage** from *Storage* tab. Check the rules again.   
 
``service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}``

