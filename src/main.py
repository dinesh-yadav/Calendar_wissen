import sys
import datetime
from PyQt5.QtWidgets import QApplication, QSystemTrayIcon, QMenu, QAction, QCalendarWidget, QWidget, QVBoxLayout, QLabel
from PyQt5.QtCore import QTimer
from PyQt5.QtGui import QIcon
import holidays

class CalendarWidget(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Calendar Widget")
        layout = QVBoxLayout()

        self.calendar = QCalendarWidget()
        self.calendar.clicked.connect(self.show_date_info)
        layout.addWidget(self.calendar)

        self.label = QLabel("Select a date")
        layout.addWidget(self.label)

        self.setLayout(layout)

    def show_date_info(self, date):
        us_holidays = holidays.US()
        if date.toPyDate() in us_holidays:
            info = f"Holiday: {us_holidays[date.toPyDate()]}"
        else:
            info = "No holiday"
        self.label.setText(info)

class SystemTrayApp:
    def __init__(self):
        self.app = QApplication(sys.argv)
        self.app.setQuitOnLastWindowClosed(False)

        # Create system tray icon
        self.tray_icon = QSystemTrayIcon()
        self.tray_icon.setIcon(QIcon("icon.png"))  # Placeholder icon
        self.tray_icon.setVisible(True)

        # Create menu
        menu = QMenu()
        show_action = QAction("Show Calendar")
        show_action.triggered.connect(self.show_calendar)
        menu.addAction(show_action)

        quit_action = QAction("Quit")
        quit_action.triggered.connect(self.app.quit)
        menu.addAction(quit_action)

        self.tray_icon.setContextMenu(menu)
        self.tray_icon.show()

        self.calendar_widget = CalendarWidget()

    def show_calendar(self):
        self.calendar_widget.show()

    def run(self):
        sys.exit(self.app.exec_())

if __name__ == "__main__":
    tray_app = SystemTrayApp()
    tray_app.run()