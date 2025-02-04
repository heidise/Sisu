package fi.tuni.prog3.sisu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

/**
 * This class is used to design the study structure tab.
 * @author shuang.fan@tuni.fi and heidi.seppi@tuni.fi 
 */
public class StudyStructureTab {
    public static HashMap<String, ArrayList<CheckBox>> _checkboxesMap;
    
    /**
     * Initialize the left view in Study Structure-tab by the given user degree module.
     * @param leftView the left pane view in Study Structure-tab.
     * @param rightView the right pane view in Study Structure-tab.
     * @param message the error/info massage Label.
     * @param degreeName Name of the user's selected degree.
     * @param degreeStructureList List containing the selected degree's structure.
     * @exception IOException if there has any IO error
     */
    public static void initialize(ScrollPane leftView, 
            ScrollPane rightView, 
            Label message,
            String degreeName,
            ArrayList<Degrees.Module> degreeStructureList)  throws IOException {
        
        _checkboxesMap = new HashMap<>();
        var keyRoot = degreeName;
        TreeItem<String> rootItem = new TreeItem<> (keyRoot);
        //var checkboxRoot = new CheckBox(keyRoot);
        rootItem.setExpanded(true);
        rightView.setContent(null);

        if (degreeStructureList.isEmpty() == false) {
            for (Degrees.Module module : degreeStructureList) {
                var key = module.getName();
                TreeItem<String> newItem = new TreeItem<> (key);
                
                if (module.getChildren().isEmpty() == true) {
                    newItem.setExpanded(false);
                    String value = newItem.getValue();
                    var checkbox = new CheckBox(value);
                    addToCheckBoxMap(key,checkbox);
                    rootItem.getChildren().add(newItem);
                }
                else {
                    setTreeItem(module, newItem);
                    rootItem.getChildren().add(newItem);
                }
            }

            TreeView<String> tree = new TreeView<> (rootItem); 
            leftView.fitToWidthProperty();
            leftView.setContent(tree);

            tree.getSelectionModel().selectedItemProperty()
                    .addListener(new ChangeListener<TreeItem<String>>() {
                @Override
                public void changed(
                    ObservableValue<? extends TreeItem<String>> observable,
                    TreeItem<String> old_val, TreeItem<String> new_val) {
                    String selectedItem = new_val.getValue();
                    var parentItem = new_val.getParent();
                    if (parentItem != null) {
                        if (_checkboxesMap.containsKey(selectedItem)) {
                            initialize_rightView_checkboxes(rightView, message,
                                _checkboxesMap.get(selectedItem));
                        }
                        else if (_checkboxesMap.containsKey(parentItem.getValue())) {
                            if (new_val.getChildren() != null) {
                                boolean hasChildrenGroup = false;
                               for (TreeItem<String> childItem : new_val.getChildren()) {
                                   if (_checkboxesMap.containsKey(childItem.getValue())) {
                                       hasChildrenGroup = true;
                                   }
                                }
                               if (hasChildrenGroup) {
                                   initialize_rightView_text(rightView, selectedItem);
                               }
                               else {
                                initialize_rightView_checkboxes(rightView, message,
                                _checkboxesMap.get(parentItem.getValue()));
                               }
                            }
                        }
                        else {
                            initialize_rightView_text(rightView, selectedItem);
                        }
                    }
                    else {
                        initialize_rightView_text(rightView, selectedItem);
                    }

                }
            });
        }
        
        // Read the existed study file to get the finished courses to select.
        var studentnumber = LoginController.ID;
        var courses = readStudyFile(studentnumber);
        if (!courses.isEmpty()) {
            for (var key : _checkboxesMap.keySet()) {
                for (var checkbox : _checkboxesMap.get(key)) {
                    for (var course : courses) {
                        if (checkbox.getText().equals(course)) {
                            checkbox.setSelected(true);
                        }
                    }
                }
            }
        }
    }
    
    /**
    * Sets recursively new children items to the displayed degree tree structure.
    * Also calls another funcion to add checkable boxes to the right side.
    * @param module Module which is part of the degree and has information of it.
    * @param parentItem Parent tree in which items are added.
    */
    public static void setTreeItem(Degrees.Module module, TreeItem<String> parentItem) {
        if (module.getChildren().isEmpty() == true) {
            parentItem.setExpanded(false);
            String value = parentItem.getValue();
            String parent_key = parentItem.getParent().getValue();
            var checkbox = new CheckBox(value);
            addToCheckBoxMap(parent_key,checkbox);
        } else {
            parentItem.setExpanded(true);
            for (Degrees.Module newModule : module.getChildren()) {
                int credits = newModule.getMinCredits();
                TreeItem<String> newItem;
                
                if (credits > 0) {
                    var key = newModule.getName() + ", " + credits + " cr";
                    newItem = new TreeItem<>(key);
                }
                else {
                    var key = newModule.getName();
                    newItem = new TreeItem<>(key);
                }
                parentItem.getChildren().add(newItem);
                setTreeItem(newModule, newItem);
            }
        }
    }
    
    /**
     * Adds checkable courses to checkboxesMap's list.
     * @param key the course name
     * @param checkbox the course checkbox
     */
    public static void addToCheckBoxMap(String key,CheckBox checkbox) {
            ArrayList<CheckBox> currentCheckBoxes = _checkboxesMap.get(key);  
            if (currentCheckBoxes == null) {
                currentCheckBoxes = new ArrayList<>();
            }
            currentCheckBoxes.add(checkbox);
            _checkboxesMap.put(key, currentCheckBoxes);
    }
    
    /**
     * Initializes the right view in Study Structure-tab to show checkable courses.
     * @param view the right scrollPane
     * @param message the error/info message of study structure saving action.
     * @param checkboxList the list of checkboxes.
     */
    public static void initialize_rightView_checkboxes(ScrollPane view, 
            Label message,
            ArrayList<CheckBox> checkboxList) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        for (CheckBox checkbox : checkboxList) {
            checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        message.setText("Don't forget to save after you checked all.");
                    }
                });
            vBox.getChildren().add(checkbox);
        }
        view.setContent(vBox);
    }
    
    /**
     * Initializes the right view in Study Structure-tab to show only text, not checkable boxes.
     * @param view the right ScrollPane
     * @param value the default text
     */
    public static void initialize_rightView_text(ScrollPane view, String value) {
        Label content = new Label(value);
        view.setContent(content);
    }
    
    // Read the selected courses from student's study file.
    private static ArrayList<String> readStudyFile(String id) throws IOException {
        var filename = String.format("src/main/students/%s.txt", id);
        try (var file = new BufferedReader(new FileReader(filename))) {
            var courses = new ArrayList<String>();
            var lines = file.lines().toArray();
            for (var l : lines) {
                var line = (String)l;
                courses.add(line);
            }
            return courses;
        }
    }
}
