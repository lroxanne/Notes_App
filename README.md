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
- **Search**: User can search for notes at home page(press search button). (**Ruoxuan Li**)
- **Add Images**: Users can add images to their notes on the edit note page. (**Ruoxuan Li**)

## Other Functions：
- ~~**Search words**: Lingua Robot API supports users to search a word's meaning (**Yuhan Peng**)~~
- **Stat**: calculate the total words in the note and show ~~(use toast but need polish)~~ (**Ruoxuan Li**)
- **Check Words**: check if the user's spelling is right (**Ruoxuan Li**)
- ~~**Change Font**: Google Font API supports hundreds of thousands of fonts for users to choose (**Yuhan Peng**)~~
- **Change Font**: implement 2 different fonts for users to choose (**Ruoxuan Li**)

## Tech:

- **Language**: Kotlin
- **Structure**: MVVM (Model-View-ViewModel)
- **APIs / Libraries**:
  - LiveData
  - ViewModel
  - DataBinding
  - Navigation Component
  - Room Database
  - Google OAuth2
  - Google Drive API
  - ~~Lingua Robot API (dropped)~~
  - ~~Google Font API (implement some of the fonts into the app)~~
  - Grammarbot API
 
## Progress:
- **Link to our First presentation slide**:
    - https://docs.google.com/presentation/d/1-PhKBUz6BZXjfQ-_IoqxucheBnkmnISiEKFgLeH91r8/edit?usp=sharing

- **Link to our Second presentation slide**:
    - https://docs.google.com/presentation/d/1zgome5nhVw2duotVGnDWhl_KOt4mEWw9qdSYCz9z52A/edit?usp=sharing

- **Link to our Third presentation slide**:
    - https://docs.google.com/presentation/d/1WWfYTXJpdB9b0acWoilSth_R6v2q3-JgnF_e7WWBZm0/edit#slide=id.g1f58920f515_0_45
   
- **Link to our previous repo**
    - https://github.com/lroxanne/note.git

- **Link to our video demo for version 0.0.3**
    - https://drive.google.com/file/d/1vfA1Siu2NelsI0suk0YC0cQCIAne8SMq/view?usp=sharing
   
- **Link to our video demo for version 1.3.0**
    - https://drive.google.com/file/d/1903y5__lIDkBcer429IduDH0u3K8lG5I/view?usp=sharing
     
## Version Control:

- version 0.0.1: Empty file with basic UI (<ins>**first presentation**</ins>)
- version 0.0.2: Finish buttons implemented on the main page
- version 0.0.3: Create a new .xml file for future navigations
- version 0.0.4: Implement Room Database but not working
- version 0.0.5: Failed to implement previous database ideas, starting over a new version of app (<ins>**second presentation & previous repo**</ins>)
- version 0.1.0: Design a new UI for this app, including using new colors, themes, and fonts
- version 1.0.0: Finish implementing new database and users can add and delete notes, which makes the app compilable
- version 1.1.0: Add a Word Count button and finish its function
- version 1.1.1: Redesign the UI for menu_edit_note.xml for new functions
- version 1.1.2: Implement Trash page, rename main page to Note page, and add Delete button and Recover button
- version 1.2.0: Finish functions and navigations needed for Note-Trash logic
- version 1.2.1: Finish functions that could convert note files to PDF files
- version 1.2.2: Implement Google OAuth2 and Google Drive API
- version 1.3.0: Users can upload note files as PDF files to their Google Drive once they log in with their Google account (<ins>**third presentation & first video demo**</ins>)
- version 1.3.1: Fix the search bar and the search tool this time is really powerful and accurate
- version 1.3.2: Polish the toast for the Word Count function
- version 1.4.0: Users are able to add images to their notes right now
- version 1.4.1: Users can change the font in the edit note page
- version 2.0.0: Implement grammar check, unknown English words will have red or blue underlines
