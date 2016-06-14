# appez-android
Featuring:- 

1.	UI Services:- This service helps provide informational and interactional components such as dialogs of varying types from the respective native platforms. Which are as:-

a.	Activity Indicator:- To keep the user informed about a long, pending operation, activity indicator can be used. It blocks the UI of the application and hence, user cannot interact with any of the components of its page.

b.	Loading Indicator:- This UI component is supported on iOS only. This component can be used when the application does not intend to block the user interface while a pending operation is underway in background. The loading indicator is thus not visible on application page but on the status bar of the iOS devices.
Here, you can also use Services like “Show Loading Indicator” & “Hide Loading Indicator”

c.	Information Dialog:- This dialog is meant for informing the user of an action that is about to take place or has taken place in the application. In this case, the user does not have control/ choice over the action as it is only for informational purpose.

d.	Decision Dialog:- This dialog is used when the application wants the user to take a decision on a flow. This dialog has a positive button as well as negative button. Any button that the user selects is communicated to the web layer. The application can then act according to the selection of the user.

e.	Single Choice List Dialog:- This operation shows a list dialog whose elements have been specified by the user. User can now select an element from the list. Then the information regarding the selected element is sent back to the web layer. Also Provide Single “Choice List Dialog with Radio”.

f.	Multiple Choice List Dialog:- This operation shows a list dialog whose elements have been specified by the user. User can now select multiple elements from the list. Then the information regarding the selected indices is sent back to the web layer.

g.	Date Picker:- As the name suggests this operation shows a date picker to the user from where it can select the date of its choice. The selected date is then communicated back to the web layer in a specified format.


2.	HTTP Service:- HTTP service facilitates the execution of network requests of varying types. User can execute HTTP requests of type GET, POST, PUT, DELETE. Additionally, this service also provides the facility to save the response of network request in a file specified by the user. It include:-
	
a.	HTTP request without save data:- Executes the network HTTP request according to the details provided by the user and returns the response from the server converted in the JSON form to the web layer. Also, the response sent to the web layer provides the response headers along with the server response.

b.	HTTP request with save data:- Performs the same operation as the HTTP request without save data, except the fact that it saves the response in a file whose name is specified by the user and returns the absolute location of the file to the web layer rather than the server response. Although, this operation returns the file location where the response was written, it still sends the response headers along with the response.


3.	Persistence service:- The purpose of this service is to provide the user with an option to save the data in the persistence store of the application. This data will be preserved across sessions of the application. This means if the user closes/ quits/ force quits the application and then reopens the application, then the same set of data is maintained in the application memory. However, if the user deliberately clears the application data or uninstalls the application, then the data would get lost. This service provides operations to save, retrieve or delete data in key-value format in the store.
	

4.	Device Database Service:- For storing the application information in a more structured format, Device Database service can be used. This service makes use of the SQLite databases which are supported by all the major platforms such as Android, iOS and Windows Phone. This service thus supports CRUD operations as well complex queries as supported by SQLite standard. Using this service user can maintain relational databases in their application in a secure manner.


5.	Maps Service:- Maps are an important part of any enterprise application and can be used for variety of purposes from user location tracking to depicting user’s Points of Interests (PoIs) etc. The map service in the appIt MMI layer tries to address this requirement of a typical enterprise application. Given the points that need to be plotted, the Map service shows the map to the user. Additionally the user has the option to get directions to a specific point from the current location of the user. This service makes use of platform specific map services i.e. Google Maps for Android, Apple Maps for iOS and Bing Maps for Windows Phone. Include operations like:-

a.	Show Map:- This operation takes the set of points to be shown on the map, from the user and plots them on the map. It also shows the user’s current location on the map.

b.	Show map with Directions.

c.	Show map with Animation.


6.	File Service:- File Service is meant to cater to various file related operations. Currently supported operations include reading file or folder contents and zipping or unzipping files. Include operations like:-

a.	Read file content.

b.	Read folder content.

c.	Zip response content.&

d.	Unzip file content.


7.	Camera Service:- Camera is one of the most important hardware resources in a device that is of particular use for Enterprise application as it can be used for information capturing using the camera hardware in the device. This service include operations like:-

a.	Image from camera:- This operation lets the user capture the image using the device camera. It also allows the user to apply couple of basic filters on the images namely Sepia and Monochrome. The quality of the captured image can be controlled by the request to be initiated by the user.

b.	Image from Gallery:- This operation lets the user capture the image using the device’s image gallery. It also allows the user to apply couple of basic filters on the images namely Sepia and Monochrome. The quality of the captured image can be controlled by the request to be initiated by the user.


8.	Graph Service:- This operation let the user to utilize various graphs to perform  comparison type of operations within the app. It include Graphs like Bar Graphs, line Graphs & Pie Charts.
