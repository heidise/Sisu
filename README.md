# Programming 3 group project: SISU

SISU is a study planning application implemented with Java. The program fetches data from Kori API (degree and course information) and updates the graphical user interface according to user’s choices. User can register, log in, view and change their study plans and also modify their own user information.

**See more information on the project documentation which can be found under /Documentation folder.**

Group members and word division:
- Heidi Seppi: Backend (fetching, handling and modifying data from API)
- Shuang: Frontend (GUI)
- Venla: Testing

**Running the code:**

SISU is a Java Maven project, so to run it you can use Apache Netbeans or use the terminal:

- `mvn clean install`
- `mvn javafx:run`

Known fault is that fetching the data lots of some time when the user logs in to the system. In the documentation, known faults have been explained.

Some snippets of the project (login view and study planning view):

![login view](https://github.com/heidise/Sisu/blob/main/Documentation/login_view.PNG)

![study planning view](https://github.com/heidise/Sisu/blob/main/Documentation/Study_planning_view.PNG)
