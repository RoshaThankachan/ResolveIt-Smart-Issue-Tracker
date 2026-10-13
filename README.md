# 📱 ResolveIt – Smart Issue Tracker

An Android application developed using **Java, XML, and SQLite** to help users report issues, track their progress, and view status updates.

---

## 📌 Overview

ResolveIt is a smart issue-tracking application designed to organize issue reporting and resolution through a mobile interface.

The application supports separate User and Admin roles. Users can register, log in, report issues, and track their status, while administrators can manage reported issues through status updates.

---

## ✨ Features

- 🔐 User registration and login
- 👥 User and Admin roles
- 📝 Issue reporting
- 📋 My Issues screen
- 🔎 Issue status tracking
- 🔄 Issue status updates
- 🛠️ Administrative issue management
- 🗄️ Local database integration using SQLite
- 📱 Android-based user interface

---

## 🔄 Application Workflow

1. **Register:** Create a user account.
2. **Login:** Access the application.
3. **Report Issue:** Submit a new issue.
4. **Track Issue:** View previously reported issues.
5. **Admin Update:** An administrator updates an issue's progress.
6. **Resolve Issue:** The issue status can be changed to Resolved.

---

## 📊 Issue Statuses

| Status | Description |
|---|---|
| Pending | The issue is awaiting action. |
| In Progress | Work is being carried out on the issue. |
| Resolved | The issue has been marked as resolved. |


---

## 🛠️ Technologies Used

**Programming Language**
- Java

**User Interface**
- XML layouts

**Database**
- SQLite
- SQLiteOpenHelper

**Development Environment**
- Android Studio

**UI Components**
- RecyclerView

---

## 🗄️ Database

The application uses SQLite through `SQLiteOpenHelper` for local data management.

The planned database entities include:

- **Users:** User accounts and roles
- **Issues:** Reported issue information and status
- **Issue_Updates:** Updates associated with reported issues

The actual table columns and relationships should be documented from the implemented database helper.

---

## ⚙️ Installation and Setup

### Prerequisites

- Android Studio
- A compatible Android SDK
- An Android emulator or compatible Android device

### Steps

1. Open Android Studio.
2. Open the ResolveIt project directory.
3. Allow Gradle synchronization to complete.
4. Select a compatible emulator or connected device.
5. Build and run the application.

Use the project's existing Gradle configuration to determine the exact Android SDK and build requirements.

---

## 🚀 Advantages

- Organizes issue reporting in a single application
- Allows users to track issue progress
- Separates user and administrator functionality
- Provides structured issue status updates
- Uses a local database for issue-related information
- Offers a foundation for future issue-management features

---

## 🔮 Future Enhancements

- Push notifications for status changes
- Issue priority and category management
- Image attachments for reported issues
- Search and filtering
- Issue history and reporting
- Backend synchronization across devices
- Improved accessibility and user experience

---

## 👩‍💻 Author

**Rosha Thankachan**

GitHub: [RoshaThankachan](https://github.com/RoshaThankachan)

---
