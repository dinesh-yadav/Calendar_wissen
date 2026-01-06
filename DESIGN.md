# Calendar Widget Design Document

## Overview
The Calendar Widget is a Java desktop application that provides a system tray calendar with holiday integration and vacation planning features. The application is built using Java 21, follows modular design principles, and is organized into packages for maintainability.

## Requirements Mapping

### 1. Basic Calendar View
- **Description**: Display a 3-month rolling calendar view (previous, current, next), allow navigation between months, highlight the current day.
- **Mapped Files**:
  - `CalendarView.java`: Handles the UI display of the 3-month calendar, navigation buttons, and visual highlighting.
  - `CalendarController.java`: Manages navigation logic and event handling for calendar interactions.

### 2. Holiday Integration
- **Description**: Connect to online calendars to display holidays, show holidays from past 11 months to future 11 months.
- **Mapped Files**:
  - `HolidayService.java`: Fetches holiday data from the Nager.Date API for multiple years.
  - `Holiday.java`: Data model for holiday information.
  - `CalendarView.java`: Displays holiday tooltips and bold formatting for holiday days.
  - `CalendarController.java`: Handles popup display of holiday details on date selection.

### 3. Work Holiday Focus
- **Description**: Visually distinguish between regular holidays and work holidays, prioritize displaying work holidays when multiple occur on the same day.
- **Mapped Files**:
  - `HolidayService.java`: Categorizes holidays as "regular" or "work" based on name patterns.
  - `CalendarController.java`: Sorts and displays work holidays first in popups.
  - `CalendarView.java`: Provides tooltips distinguishing holiday types.

### 4. Vacation Planning Aid
- **Description**: Provide visual indicators for weeks containing work holidays, differentiate between weeks with single vs multiple work holidays.
- **Mapped Files**:
  - `CalendarView.java`: Implements week highlighting with color-coded text (cyan for single, blue for multiple work holidays in the week).

### 5. System Integration
- **Description**: Auto-start option, minimal resource usage.
- **Mapped Files**:
  - `AutoStartManager.java`: Handles enabling/disabling auto-start by managing startup folder shortcuts.
  - `HolidayService.java`: Optimized to fetch only necessary years for minimal resource usage.
  - `CalendarWidget.java`: Main class managing system tray integration.

### 6. Modularization and Design Principles
- **Description**: Modularize the app using SOLID, Observer, Strategy, Facade design patterns.
- **Mapped Files**: All files follow these principles as described below.

## Design Principles

### SOLID Principles
- **Single Responsibility**: Each class has one primary responsibility.
  - `HolidayService`: Only handles holiday data fetching and processing.
  - `CalendarView`: Only manages UI display and visual elements.
  - `CalendarController`: Only handles user interactions and business logic.
  - `AutoStartManager`: Only manages auto-start functionality.
- **Open-Closed**: Classes are open for extension but closed for modification.
  - `HolidayService` can be extended with new data sources without changing existing code.
  - `CalendarView` can add new visual indicators without altering core display logic.
- **Liskov Substitution**: Interfaces and base classes allow substitution.
  - Holiday data structures are consistent across modules.
- **Interface Segregation**: No unnecessary dependencies.
  - Each module has focused interfaces (e.g., HolidayService methods are specific to holiday operations).
- **Dependency Inversion**: High-level modules don't depend on low-level modules.
  - `CalendarWidget` depends on abstractions (interfaces) rather than concrete implementations.

### Observer Pattern
- **Implementation**: Used for handling calendar date changes.
- **Mapped Files**:
  - `CalendarController.java`: Registers PropertyChangeListener on JCalendar components to observe date selections.
  - Enables reactive updates when users interact with the calendar.

### Strategy Pattern
- **Implementation**: Allows different strategies for holiday data sources.
- **Mapped Files**:
  - `HolidayService.java`: Can be implemented as a strategy interface, allowing different providers (API, local file, etc.).
  - Currently implemented as a concrete class, but designed for easy strategy swapping.

### Facade Pattern
- **Implementation**: Simplifies complex subsystem interactions.
- **Mapped Files**:
  - `CalendarController.java`: Acts as a facade, providing a simple interface for calendar operations while coordinating between view and service layers.
  - `CalendarWidget.java`: Main facade coordinating all subsystems (tray, view, controller).

## Module Structure

```
src/
├── main/java/com/example/calendarwidget/
│   ├── CalendarWidget.java          # Main application class, system tray
│   ├── controller/
│   │   └── CalendarController.java  # Navigation and event handling
│   ├── model/
│   │   └── Holiday.java             # Holiday data model
│   ├── service/
│   │   └── HolidayService.java      # Holiday data fetching service
│   ├── util/
│   │   └── AutoStartManager.java    # Auto-start utility
│   └── view/
│       └── CalendarView.java        # Calendar UI and highlighting
└── test/java/com/example/calendarwidget/
    └── model/
        └── HolidayTest.java         # Unit tests
```

## Class Descriptions

### CalendarWidget
- **Purpose**: Main application entry point.
- **Responsibilities**: System tray management, menu creation, application lifecycle.
- **Design Principles**: Facade for subsystem coordination.

### Holiday
- **Purpose**: Data model for holiday information.
- **Responsibilities**: Store holiday date, name, and type.
- **Design Principles**: Single Responsibility, encapsulates holiday data.

### HolidayService
- **Purpose**: Service for fetching and processing holiday data.
- **Responsibilities**: API communication, data parsing, holiday categorization.
- **Design Principles**: Single Responsibility, Strategy (easily replaceable data source).

### CalendarView
- **Purpose**: UI component for calendar display.
- **Responsibilities**: Render 3-month view, handle visual indicators, tooltips.
- **Design Principles**: Single Responsibility, Open-Closed (new indicators can be added).

### CalendarController
- **Purpose**: Controller for calendar interactions.
- **Responsibilities**: Handle navigation, event processing, coordinate view updates.
- **Design Principles**: Single Responsibility, Observer (listens to UI events), Facade.

### AutoStartManager
- **Purpose**: Utility for system integration.
- **Responsibilities**: Manage auto-start functionality.
- **Design Principles**: Single Responsibility, follows SOLID.

## Testing
- Unit tests are located in `src/test/java/`
- Uses JUnit 5 for testing
- Currently tests the Holiday model
- Additional tests can be added for other components

## Dependencies
- JCalendar: Calendar UI component
- Gson: JSON parsing for API responses
- JUnit: Unit testing framework

This design ensures maintainability, extensibility, and adherence to software engineering best practices.