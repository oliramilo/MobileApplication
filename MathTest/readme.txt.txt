The application consists of 9 Activities 


MathTestActivity:
	Math test that connects to the server, queries questions to be displayed in the activity, clickable buttons with up to 4 options on display at a time

EmailerActivity:
	Emails test history for specified Students in the database, we can select multiple students to be emailed


LoginActivity:
	Login activity for users to autheticate and login as their user for the applcication

SelectedUserActivity: 
	When a student is selected from a list of students, it prompts to this activity which shows user info on the selected user, we can add/remove/delete users

SearchPhotoActivity:
	Searches specified images using the pixabay api, returns at most 50 images on a query of images

ViewUsersActivity:
	User viewer for multiple Students/Users, this activity uses a recycler view which shows a scrollable list of users for the specified user, On click edit will
	change activitiy to SelectedUserActivity which we could edit/delete user or view their test history	

ViewTestsActivity:
	Test viewer for a single student/user, this activity uses a recycler view which shows a scrollable list of test submissions for the specified user

DatabaseHandler:
	Database handler for creating the database and tables, this is needed for the entire database

TestHandler:
	Database handler for test submissions, we can only add test submissions to users and remove them if a user is deleted.


UserHandler:
	Database handler for Users table, remove,add,edit,authenticate,duplicate operations to manage user info in the user table

UserImageHandler:
	Database handler for the Images table, remove,add,edit operations to manage user info in the images table

AppAlerter:
	Uses Toast to display messages to the user when events

InputValidator:
	Validates user input, using regexes for pattern matching within user inputs we can detect invalid user inputs.


DashboardFragment:
	DashboardFragment is a fragment used for the main activity to display user's full name and profile image

SessionManager:
	Is a session manager for user that is logged in, we can keep track of the user details using SessionManager when a user is logged in without passing data from activity
	to activity or using database.

