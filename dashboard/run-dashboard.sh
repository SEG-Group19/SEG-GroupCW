#!/bin/bash


JAR_FILE="target/adauction-app-0.4.1.jar"


JAVAFX_PATH="/Users/constantinospsaras/javafx-sdk-17.0.14/lib"

# Run the application with JavaFX modules
java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -jar "$JAR_FILE"
