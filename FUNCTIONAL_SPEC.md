# Calendar Widget Functional Specification

## Introduction
The Calendar Widget is a Java desktop application that provides a system tray-based calendar with holiday integration and vacation planning features. It displays a 3-month rolling calendar view with navigation, highlights work holidays, and offers auto-start functionality.

## Prerequisites
- **Java 17+**: JDK 17 or higher must be installed and configured (tested with Java 17 and 21)
- **Maven 3.9+**: For building and running the project (optional - manual compilation also supported)
- **Internet Connection**: Required for fetching holiday data from API
- **Windows OS**: Designed for Windows system tray integration

## Running the Project

### Option 1: Using Maven (Recommended if available)

#### Step 1: Clone/Download the Project
Ensure the project files are in a directory, e.g., `C:\Users\Wissen\Desktop\Calendar_wissen`

#### Step 2: Set Up Environment
1. Install JDK 17+ if not already installed
2. Install Maven 3.9+ if not already installed
3. Ensure `JAVA_HOME` points to JDK installation
4. Add Maven to system PATH

#### Step 3: Build the Project
Open a command prompt in the project directory and run:
```bash
mvn clean compile
```
This compiles all source files and downloads dependencies.

#### Step 4: Run the Application
Execute the following command:
```bash
mvn exec:java -Dexec.mainClass=com.example.calendarwidget.CalendarWidget
```

### Option 2: Manual Compilation (Alternative)

#### Step 1: Download Dependencies
The required JAR dependencies are already included in the `lib/` directory:
- `jcalendar-1.4.jar`: Calendar UI component library
- `gson-2.10.1.jar`: JSON processing library

#### Step 2: Compile the Project
Compile all Java source files in dependency order:
```bash
javac -cp "lib\*" -d target\classes src\main\java\com\example\calendarwidget\model\*.java src\main\java\com\example\calendarwidget\service\*.java src\main\java\com\example\calendarwidget\view\*.java src\main\java\com\example\calendarwidget\controller\*.java src\main\java\com\example\calendarwidget\util\*.java src\main\java\com\example\calendarwidget\*.java
```

#### Step 3: Run the Application
Execute with classpath including compiled classes and dependencies:
```bash
java -cp "target\classes;lib\*" com.example.calendarwidget.CalendarWidget
```

### Step 4: Using the Application
- The application will start and add an icon to the system tray
- Right-click the tray icon to access the menu
- Select "Show Calendar" to display the 3-month calendar window

### Step 5: Application Features
- **Navigation**: Use "< Previous" and "Next >" buttons to navigate months
- **Holiday View**: Hover over dates to see holiday tooltips
- **Date Selection**: Click on dates to view detailed holiday information in popups
- **Auto-start**: Use "Enable Auto-start" from tray menu to launch app on Windows startup
- **Exit**: Use "Exit" from tray menu to close the application

## Testing

### Unit Tests
Run unit tests using Maven (if available):
```bash
mvn test
```
Or run manually with Java:
```bash
java -cp "target\classes;lib\*" org.junit.platform.console.ConsoleLauncher --scan-classpath
```
- Tests are located in `src/test/java/`
- Currently includes tests for the Holiday model
- Output shows test results: passed/failed counts

### Integration Testing
1. **Holiday Fetching**: Verify holidays are loaded by checking console output during startup
2. **UI Testing**: Manually test calendar navigation, highlighting, and tooltips
3. **System Tray**: Verify tray icon appears and menu functions work
4. **Auto-start**: Test enabling/disabling auto-start and verify on system restart

### Manual Testing Checklist
- [ ] Application starts without errors
- [ ] System tray icon appears
- [ ] Calendar window displays 3 months
- [ ] Navigation buttons work correctly
- [ ] Current day is highlighted
- [ ] Holiday dates show tooltips on hover
- [ ] Clicking dates shows holiday popups
- [ ] Work holidays are prioritized in displays
- [ ] Week highlighting works (colored text for work holiday weeks)
- [ ] Auto-start can be enabled/disabled
- [ ] Application exits cleanly

## Logging and Debugging

### Logging Configuration
The application uses Java Util Logging (JUL) with the following configuration:
- **Configuration File**: `src/main/resources/logging.properties`
- **Log Levels**:
  - `INFO`: General application flow and important events
  - `FINE`: Detailed debugging information
  - `SEVERE`: Error conditions and exceptions
- **Output**: Console output with timestamp, level, class, and message

### Key Logging Points
1. **Application Startup**: Logs initialization steps and component setup
2. **Holiday Loading**: Logs API calls, response status, and data processing
3. **UI Interactions**: Logs menu clicks, navigation, and date selections
4. **File Operations**: Logs auto-start enable/disable operations
5. **Error Handling**: Logs exceptions with stack traces for debugging

### Viewing Logs
- Logs appear in the console when running the application
- During startup, you'll see logs like:
  - "Starting Calendar Widget application"
  - "HolidayService initialized"
  - "Holiday data loaded successfully"
  - "Tray icon added to system tray successfully"
  - "Calendar Widget application initialized successfully"
- For detailed debugging, set log level to `FINE` in `logging.properties`
- Error logs include full stack traces for troubleshooting

### Common Debug Scenarios
- **Network Issues**: Check holiday loading logs for HTTP status codes
- **UI Problems**: Look for calendar update and highlighting logs
- **File System Errors**: Monitor auto-start operation logs
- **Initialization Failures**: Review startup sequence logs

## Detailed File Explanations

### Main Application Files

#### `pom.xml`
- **Purpose**: Maven project configuration file
- **Key Elements**:
  - Project metadata (groupId, artifactId, version)
  - Java version configuration (source/target: 21)
  - Dependencies: JCalendar, Gson, JUnit
  - Build plugins: Maven Compiler, Surefire (for tests), Exec (for running)
- **Dependencies**:
  - `jcalendar`: Calendar UI component
  - `gson`: JSON parsing for API responses
  - `junit-jupiter`: Unit testing framework

#### `run.bat`
- **Purpose**: Windows batch script for auto-start functionality
- **Content**: Sets environment variables and runs the Maven exec command
- **Usage**: Copied to Windows startup folder when auto-start is enabled

### Source Code Files

#### `com/example/calendarwidget/CalendarWidget.java`
- **Purpose**: Main application class and entry point
- **Key Methods**:
  - `main()`: Initializes application, sets up system tray, creates UI components
  - `showCalendar()`: Displays the calendar window
- **Responsibilities**:
  - System tray icon management
  - Menu creation (Show Calendar, Auto-start, Exit)
  - Application lifecycle management
- **Dependencies**: All other modules (controller, view, service, util)

#### `com/example/calendarwidget/model/Holiday.java`
- **Purpose**: Data model class for holiday information
- **Fields**:
  - `date`: String representation of holiday date
  - `name`: Holiday name
  - `type`: "regular" or "work" classification
- **Methods**: Standard getters/setters and constructors
- **Usage**: Stores parsed holiday data from API responses

#### `com/example/calendarwidget/service/HolidayService.java`
- **Purpose**: Service class for fetching and processing holiday data
- **Key Methods**:
  - `loadHolidays()`: Fetches holiday data from Nager.Date API for 3 years
- **Responsibilities**:
  - HTTP client management for API calls
  - JSON parsing using Gson
  - Holiday categorization (regular vs work)
  - Data aggregation into Map structure
- **Dependencies**: Gson for JSON parsing, Java HTTP client

#### `com/example/calendarwidget/view/CalendarView.java`
- **Purpose**: UI component for calendar display and visual elements
- **Key Methods**:
  - `updateCalendars()`: Updates the 3-month display
  - `highlightWeeks()`: Applies visual indicators for work holiday weeks
- **Responsibilities**:
  - Creating and managing JCalendar components
  - Implementing week highlighting (text color changes)
  - Managing tooltips for holiday information
  - Handling bold formatting for holiday days
- **Dependencies**: JCalendar library, Holiday model

#### `com/example/calendarwidget/controller/CalendarController.java`
- **Purpose**: Controller class managing user interactions and business logic
- **Key Methods**:
  - `navigatePrevious()` / `navigateNext()`: Handle month navigation
  - Setup of event listeners for calendar interactions
- **Responsibilities**:
  - Coordinating between view and model
  - Handling date selection events
  - Managing popup displays for holiday information
  - Sorting holidays by priority (work first)
- **Dependencies**: CalendarView, Holiday model

#### `com/example/calendarwidget/util/AutoStartManager.java`
- **Purpose**: Utility class for managing auto-start functionality
- **Key Methods**:
  - `enableAutoStart()`: Copies batch file to startup folder
  - `disableAutoStart()`: Removes batch file from startup folder
- **Responsibilities**:
  - File system operations for startup integration
  - Error handling for file operations
- **Dependencies**: Java NIO for file operations

### Test Files

#### `com/example/calendarwidget/model/HolidayTest.java`
- **Purpose**: Unit tests for the Holiday model class
- **Test Methods**:
  - `testHolidayCreation()`: Tests constructor and field initialization
  - `testHolidaySetters()`: Tests setter methods and getters
  - `testHolidayDefaultConstructor()`: Tests default constructor
- **Framework**: JUnit 5
- **Coverage**: Basic functionality of Holiday class

### Documentation Files

#### `README.md`
- **Purpose**: Project overview and basic usage instructions
- **Content**: Project description, features, setup requirements

#### `DESIGN.md`
- **Purpose**: Detailed design document
- **Content**: Architecture, design patterns, requirements mapping, class descriptions

## Architecture Overview
The application follows a modular MVC-inspired architecture:
- **Model**: Holiday data structures
- **View**: Calendar UI components
- **Controller**: Business logic and event handling
- **Service**: External data fetching
- **Util**: System integration utilities

## Error Handling
- Network failures in holiday fetching are logged but don't crash the app
- File operations in auto-start include user-friendly error messages
- Invalid data from API is handled gracefully

## Performance Considerations
- Holiday data cached on startup for the session
- Minimal UI updates to reduce resource usage
- Efficient data structures (HashMap for holiday lookup)

## Future Enhancements
- Additional holiday sources
- Custom holiday categories
- Calendar themes
- Reminder notifications
- Export functionality