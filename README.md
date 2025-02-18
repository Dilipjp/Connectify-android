# Connectify - Social Media App 📸  

Connectify is a **social media application** for Android, designed to provide a seamless experience similar to Instagram. Users can **create posts, like, comment, follow other users**, and engage with content effortlessly. The app integrates **Firebase Authentication** for secure login, **Firestore** for real-time data storage, and **Picasso** for optimized image loading.

## 🚀 Features

- 📷 **Post & Share**: Users can upload images with captions.
- ❤️ **Like & Comment**: Engage with posts through likes and comments.
- 👥 **Follow System**: Follow and interact with other users.
- 🔍 **Explore & Search**: Discover new users and posts.
- 🔔 **Real-time Notifications**: Get notified of likes, comments, and new followers.
- 🌓 **Dark Mode/Light Mode**: Toggle between light and dark themes.
- 🔒 **Secure Authentication**: Users can sign up and log in using **Firebase Authentication**.
- 📂 **Optimized Image Loading**: Uses **Picasso** for fast and efficient image rendering.

---

## 🛠 Key Technologies Used

- **Java** - Core language for Android app development.
- **Firebase Authentication** - Secure user authentication and session management.
- **Firebase Firestore** - Real-time NoSQL database for storing posts, comments, and user interactions.
- **Picasso** - Image loading and caching for seamless media display.
- **Android Jetpack Components** - Modern UI and architecture patterns.
- **Dark Mode/Light Mode** - Adaptive UI themes for user preference.

---

## 📸 Screenshots

| Home Feed | Profile Page | Post Details |
|-----------|-------------|--------------|
| ![Home](https://github.com/yourusername/Connectify/blob/main/screenshots/home.png) | ![Profile](https://github.com/yourusername/Connectify/blob/main/screenshots/profile.png) | ![Post](https://github.com/yourusername/Connectify/blob/main/screenshots/post.png) |

---

## 🔧 Installation Guide

### Prerequisites:
- **Android Studio** installed
- **Firebase Project** set up with Authentication & Firestore
- **Google Services JSON** (Download from Firebase Console)

### Steps:
1. **Clone the repository**:
   ```sh
   git clone https://github.com/yourusername/Connectify.git
   cd Connectify
2. **Open in Android Studio and let dependencies sync.**
3. **Configure Firebase:**
Add google-services.json to the app/ directory.
Enable Firestore & Authentication in Firebase Console.
3. **Run the App on an emulator or physical device.**

## 📌 Functional Requirements  

✅ **User Registration & Login** – Secure authentication using Firebase  
✅ **Post Creation** – Users can upload images with captions  
✅ **Like & Comment System** – Engage with posts in real time  
✅ **Follow & Unfollow Users** – Users can connect and interact  
✅ **Explore & Search** – Discover new users and trending posts  
✅ **Dark Mode Support** – Toggle between light and dark themes  
✅ **Secure Data Storage** – Firestore database for storing posts & user details  
✅ **Notifications** – Users receive updates on likes, comments, and follows  

---

## 🔒 Non-Functional Requirements  

✔ **Performance** – Optimized data fetching for a smooth user experience  
✔ **Security** – Firebase Authentication for user verification and data protection  
✔ **Scalability** – Firestore handles high volumes of data and user interactions  
✔ **Accessibility** – User-friendly UI with Dark/Light Mode support  
✔ **Usability** – Intuitive navigation and seamless interaction  
✔ **Compatibility** – Works across a wide range of Android devices  
✔ **Reliability** – Ensures real-time data synchronization and app stability  
