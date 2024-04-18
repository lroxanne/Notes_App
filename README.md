# Note APP 

## Team members: 
- Ruoxuan Li
- Yuhan Peng

This is an Android application using Kotlin for managing notes on mobile devices. Users can add, edit, view, and delete notes, and also export notes as PDF files (upload to Google Drive). This application uses MVVM architecture, Room database, Google Drive API, and Lingua Robot API... (more features to be implemented in the future).

## Main Functions:

- **Add Note**: Users can create new notes, enter text, and save them. (**Ruoxuan Li**)
- **Edit Notes**: Users can modify and save existing notes' content. (**Ruoxuan Li**)
- **View Notes**: All notes can be viewed on the home page. (**Ruoxuan Li**)
- **Delete and Restore Notes**: Users can delete and restore unwanted notes in the recycle bin. (**Yuhan Peng**)
- **Export to PDF**: Users can export selected notes to PDF format for external sharing and printing. (**Yuhan Peng**)
- **Google Drive**: let users upload notes to Google Drive. (**Yuhan Peng**)
- **Search**: User can search for notes at home page(press search button)(**Ruoxuan Li**)

## Other Functions：
- **Search words**: Lingua Robot API supports users to search a word's meaning (**Yuhan Peng**)
- **Stat**: calculate the total words in the note and show (use toast but need polish) (**Ruoxuan Li**)

## Tech:

- **Language**: Kotlin
- **Structure**: MVVM (Model-View-ViewModel)
- **API / Libraries**:
  - LiveData
  - ViewModel
  - DataBinding
  - Navigation Component
  - Room Database
  - Google OAuth2
  - Google Drive API
  - Lingua Robot API (in progress)
  - Google Font API (considering)
  - Grammarly API (not supporting anymore)
 
  ## Progress:
  - **Link to our first presentation slide**:
    - https://docs.google.com/presentation/d/1-PhKBUz6BZXjfQ-_IoqxucheBnkmnISiEKFgLeH91r8/edit?usp=sharing

  - **Link to our Second presentation slide**:
    - https://docs.google.com/presentation/d/1zgome5nhVw2duotVGnDWhl_KOt4mEWw9qdSYCz9z52A/edit?usp=sharing

  - **Link to our Third presentation slide**:
    - https://docs.google.com/presentation/d/1WWfYTXJpdB9b0acWoilSth_R6v2q3-JgnF_e7WWBZm0/edit#slide=id.g1f58920f515_0_45
     
## Version Control:

- version 0.0.1: Empty file with basic UI (<ins>**first presentation**</ins>)
- version 0.0.2: Finish buttons implemented on the main page
- version 0.0.3: Create a new .xml file for future navigations
- version 0.0.4: Implement Room Database but not working
- version 0.0.5: Failed to implement previous database ideas, starting over a new version of app (<ins>**second presentation**</ins>)
- version 0.1.0: Design a new UI for this app, including using new colors, themes, and fonts
- version 1.0.0: Finish implementing new database and users can add and delete notes, which makes the app compilable
- version 1.1.0: Add a Word Count button and finish its function
- version 1.1.1: Redesign the UI for menu_edit_note.xml for new functions
- version 1.1.2: Implement Trash page, rename main page to Note page, and add Delete button and Recover button
- version 1.2.0: Finish functions and navigations needed for Note-Trash logic
- version 1.2.1: Finish functions that could convert note files to PDF files
- version 1.2.2: Implement Google OAuth2 and Google Drive API
- version 1.3.0: Users can upload note files as PDF files to their Google Drive once they log in with their Google account (<ins>**third presentation**</ins>)
- version 1.3.0++: Wait for considerating, designing, and coding
