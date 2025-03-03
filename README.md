# SEG-GroupCW

Our software engineering group project, **AdVantage** is an ad auction dashboard that allows the user to input and visualise their ad campaign metrics!

![GitHub Tag](https://img.shields.io/github/v/tag/danhoangg/SEG-GroupCW?sort=semver&label=semver)
![GitHub Issues or Pull Requests](https://img.shields.io/github/issues/danhoangg/SEG-GroupCW)
![GitHub Issues or Pull Requests](https://img.shields.io/github/issues-pr/danhoangg/SEG-GroupCW)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/danhoangg/SEG-GroupCW/maven.yml?label=workflow)

## Group Members
1. **Daniel Hoang** - dh3g23
2. **Constantinos Psaras** - cp2n23
3. **Luke Joseph** - lj3g23
4. **Chiebuka Ohallem** - cno1e22
5. **Zhejun Shen** - zs1d22

## Installation

### Required to install
- JDK 17
- Maven 3.8.1

### Clone
```bash
git clone https://github.com/SEG-Group19/SEG-GroupCW
```

### Run
```bash
cd SEG-GroupCW/dashboard
mvn clean javafx:run
```

## Testing
To run all the written tests,
```bash
mvn clean test
```
You may also want to run it headlessly, which can be done by running
```bash
xvfb-run -a mvn clean test
```
## File Structure
```bash
dashboard/
├── src/
│   ├── main/
│   │   ├── java/com/adauction/group19/
│   │   │   ├── controller/       # Controllers (business logic & UI control)
│   │   │   ├── model/            # Data models & business logic
│   │   │   ├── view/             # JavaFX Views (FXML + UI components)
│   │   │   ├── utils/            # Helper classes (e.g., file handling, parsing)
│   │   │   ├── service/          # Data handling & computation services
│   │   │   ├── repository/       # Handles database interactions
│   │   │   └── App.java          # Entry point for JavaFX application
│   │   └── resources/
│   │       ├── fxml/             # FXML files (separate UI from Java)
│   │       ├── css/              # Stylesheets for JavaFX UI
│   │       ├── fonts/            # Fonts for text in JavaFX UI
│   │       ├── images/           # Icons and graphics
│   │       └── config.properties # Config file (if needed)
│   └── test/                     # JUnit tests
├── pom.xml                       # Maven configuration file  
├── README.md                     # Documentation
└── .gitignore                    # Git ignores unnecessary files
```

## Contributing
To contribute read the version control documentation [here](https://github.com/SEG-Group19/SEG-GroupCW/blob/main/VERSION_CONTROL.md)
