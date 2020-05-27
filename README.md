# seleniumTesting
Writing automated test scripts using Selenium.

## Steps for creating a Selenium Project
1. Create a new folder in the editor(VSCode).
2. Select Maven Project, add quickstart with lastest version.
3. Provide groupId, artifactId, package of your choice.
4. Provide version of it. (Example 1.1)
5. Select Y
6. A Maven project project with pom would be created.
7. Copy the POM file into your project, that adds dependency. That downloads the JAR files automatically and set up the project.

## Drivers Added
1. Chrome Driver - Mac, Linux and Windows
2. IE Driver - Windows
3. Firefox(gecko) Driver - Windows

## How to change the driver?
String driverPath = "Drivers/chromedriver"; <- Here

## Json File
Json File named commandFile.json is used to pass parameters dynamically.

## Run Project from Command Line?
Path: mvn test -f "Path to pom.xml from the root/start"
eg: mvn test -f "/Library/WebServer/Documents/seleniumTesting/seleniumTesting/seleniumTest/pom.xml"

## Activity Demonstrated
1. Avoid sleeping
2. Use of Json File