package fi.tuni.prog3.sisu;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Degrees class contains information about Sisu degrees.
 * Class saves all the possible degree options and major options 
 * and updates degree structure according to which degree user chooses.
 * @author heidi.seppi@tuni.fi
 */
public class Degrees {
    private final String DEGREE_URL = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";
    private final String MODULE_URL = "https://sis-tuni.funidata.fi/kori/api/modules/";
    
    private final int STUDYMODULE = 0;
    private final int GROUPINGMODULE = 1;
    private final int COURSEUNITMODULE = 2;
    private final int ANYCOURSEUNITMODULE = 3;
    private final String ANYCOURSEUNIT_NAME = "Vapaasti valittavat opintojaksot";
    
    // Map for main degrees and list of Major objects which belong to the degree.
    private TreeMap<String, ArrayList<Major>> degreeMap;
    // List for user's selected degree information.
    private ArrayList<Degrees.Module> degreeStructureList;
    private API api = new API();
    
    /**
    * Module inner class contains information about different degree modules or courses.
    * This class is build to be treestructure and it saves all the children modules under private parameter.
    */
    public class Module extends DegreeModule {
        private ArrayList<Module> children = new ArrayList<>();
        private int moduleType;

        /**
         * Constructs a Module object with the given parameters.
         * @param name the module/course name
         * @param id the id of module/course
         * @param groupId the group id of module/course
         * @param minCredits the minimum credits of the module/course
         * @param moduleType the type of module/course
         */
        public Module(String name, String id, String groupId, int minCredits,
                int moduleType) {
            super(name, id, groupId, minCredits);
            this.moduleType = moduleType;
        }
        
        /**
         * Get the children nodes.
         * @param module the parent module.
         */
        public void addChildren(Module module) {
            this.children.add(module);
        }
        
        /**
         * Get the type of module.
         * @return the type of module as Integer.
         */
        public int getType() {
            return moduleType;
        }
        
        /**
         * Get the list of all children nodes.
         * @return the list of all children nodes.
         */
        public ArrayList<Module> getChildren() {
            return children;
        }
    }
    
    /**
    * Major inner class contains information about degree's major.
    */
    public class Major extends DegreeModule {
        
        public Major(String name, String id, String groupId, int minCredits) {
            super(name, id, groupId, minCredits);
        }
}
    
    /**
    * Changes JsonElement to String and removes "-marks from it.
    * @param element JsonElement which will be changed to String type.
    * @return JsonObject in String form.
    */
    public String elementToString(JsonElement element) {
        return element.toString().replace("\"","");
    }
    
    /**
    * Stores all the degree names and degree Ids from Kori API to TreeMap 
    * structure.
    */
    public void fetchDegreesFromApi() {
        System.out.println("Fetching data from Kori Api. This may take a while. Please wait.");
        this.degreeMap = new TreeMap<>();
        JsonObject jsonObject = api.getJsonObjectFromApi(DEGREE_URL);
        JsonArray degreeArray = jsonObject.getAsJsonArray("searchResults");
        
        String id;
        String name;
        for (JsonElement e : degreeArray) {
            id = elementToString(e.getAsJsonObject().get("id"));
            name = elementToString(e.getAsJsonObject().get("name"));
            ArrayList<Major> majorList = fetchMajorsFromApi(id);
            degreeMap.put(name, majorList);
        }
    }
    
    /**
    * Fetches certain degree's major choises from Kori Api.
    * @param id Id number which will be used to fetch data from Kori Api.
    * @return Returns ArrayList containing all the Major objects for the certain degree.
    */
    public ArrayList<Major> fetchMajorsFromApi(String id) {
        ArrayList<Major> majorList = new ArrayList<>();
        JsonObject degreeObject = api.getJsonObjectFromApi(MODULE_URL + id);
        JsonObject moduleObject = degreeObject.get("rule").getAsJsonObject();
        ArrayList<JsonObject> moduleRuleArray = handleModuleTypes(moduleObject);
        String groupId;

        for (JsonObject o : moduleRuleArray) {
            groupId = elementToString(o.get("moduleGroupId"));
            JsonObject moduleObj = api.getModuleObjectFromApi(groupId);
            if (checkIfSelectableDegree(moduleObj)) {
                String majorName = "";
                JsonObject majorNameObject = moduleObj.get("name").getAsJsonObject();
                if (majorNameObject.has("fi")) {
                    majorName = elementToString(majorNameObject.get("fi"));
                    }
                else if (majorNameObject.has("en")) {
                    majorName = elementToString(majorNameObject.get("en"));
                }
                String credits = "0";
                if (moduleObj.has("credits")) {
                    JsonObject creditObject = moduleObj.get("credits").getAsJsonObject();
                    if (creditObject.has("min")) {
                        credits = elementToString(creditObject.get("min"));
                    }
                }
                Major major = new Major(majorName,id,id,Integer.parseInt(credits));
                majorList.add(major);
            }
        }
        return majorList;
    }
    
    /**
    * Function checks if module object is major or other study module.
    * @param jsonObject JsonObject which module type will be checked.
    * @return True if module is major and can be selected by user. False if module is other kind of studymodule. 
    */
    public boolean checkIfSelectableDegree(JsonObject jsonObject) {
        Gson gson = new Gson();
        String moduleType = gson.toJson(jsonObject.get("type"));
        String require_min = "0";
        String require_max = "0";
        if (moduleType.equals("\"StudyModule\"")) {
            return true;
        }
        else if (moduleType.equals("\"CompositeRule\"") && jsonObject.has("require")) {
            JsonArray newArray = jsonObject.get("rules").getAsJsonArray();
            for (JsonElement element : newArray) {
                 if (jsonObject.get("require").isJsonNull()) {
                    return true;
                 }
                    else {
                            return checkIfSelectableDegree(newArray.getAsJsonObject());
                            }
            }
        }
        else if (moduleType.equals("\"CreditsRule\"")) {
            return false;
        }
        else {
            return false;
        }
        return false;
    }
    
    /**
    * Funcion creates complete structure for the degree according to user's selected degree and major.
    * @param degreeName Name of the selected main degree.
    * @param selectedMajor Name of the selected degree's major.
    */
    public void createStudyStructure(String degreeName, String selectedMajor) {
        System.out.println("Fetching and saving degree data. This may take a while.");
        this.degreeStructureList = new ArrayList<>();
        if (degreeMap.containsKey(degreeName)) {
            ArrayList<Major> majorList = degreeMap.get(degreeName);
            for (Major major : majorList) {
                if (major.getName().equals(selectedMajor)) {
                    String Id = major.getId();
                    JsonObject jsonObject = api.getJsonObjectFromApi(MODULE_URL + Id);
                    Gson gson = new Gson();
                    String json = gson.toJson(jsonObject);
                    String type = gson.toJson(jsonObject.get("type"));

                    if (type.equals("\"DegreeProgramme\"")) {  
                        saveModulesToStructure(jsonObject, selectedMajor);
                    }
                    else {
                        // Muuta!
                        System.out.println("Type wasn't DegreeProgramme.");
                    }
                }
            }
        }    
        else {
            System.out.println("Error! Degree can't be found.");
        }
    }

    /**
    * Recursively adds main DegreeModules to the Degree class degreeStructureList according to selected major.
    * @param jsonObject JsonObject module which has information about the course or group of courses.
    * @param selectedMajor Name of the selected degree's major which degree information will be saved.
    */
    public void saveModulesToStructure(JsonObject jsonObject, String selectedMajor) {  
        String name = "";
        String id = "";
        String groupId = "";
        int minCredits  = 0;
        JsonObject moduleObject = jsonObject.get("rule").getAsJsonObject();
        ArrayList<JsonObject> moduleRuleArray = handleModuleTypes(moduleObject);
        Module module;
        
        // Goes through all the modules in the arraylist.
        for (JsonObject o : moduleRuleArray) {
            String type = o.get("type").toString();
            if (type.equals("\"AnyCourseUnitRule\"") || type.equals("\"AnyCourseUnit\"")
                    || type.equals("\"AnyModuleRule\"")){
                module = createAnyCourseModule();
                degreeStructureList.add(module);
            }
            else {
                groupId = elementToString(o.get("moduleGroupId"));
                JsonObject moduleObj = api.getModuleObjectFromApi(groupId);

                String majorName = "";
                JsonObject majorNameObject = moduleObj.get("name").getAsJsonObject();
                if (majorNameObject.has("fi")) {
                    majorName = elementToString(majorNameObject.get("fi"));
                    }
                else if (majorNameObject.has("en")) {
                    majorName = elementToString(majorNameObject.get("en"));
                }

                if (majorName.equals(selectedMajor)) {
                    // Only selected major data will be handled and saved to structure.
                    String objType = moduleObj.get("type").toString();
                    if (objType.equals("\"StudyModule\"")) {
                        module = createModule(moduleObj, groupId);
                        degreeStructureList.add(module);
                    }
                    else if (objType.equals("\"GroupingModule\"")) {
                        Module groupingModule = createModule(moduleObj, groupId);
                        System.out.println("GROUPINGMODULE, TODO, tarkista tarvitaanko!");
                        //getModules(moduleObj);
                    }
                    else {
                        System.out.println("Muu tyyppi kuin studyModule: " + objType);
                    }
                }
            }
        } 
    } 
    
    /**
    * Function gathers all the different type of JsonObject modules into ArrayList.
    * This function calls other function to create Module objects according to JsonObject's type.
    * @param jsonObject JsonObject which will be handled and it's children objects will be filtered.
    * @return Return ArrayList containing JsonObject type modules.
    */
    public ArrayList<JsonObject> getAllModules(JsonObject jsonObject) {
        String name = "";
        String id = "";
        String groupId = "";
        int minCredits  = 0;
        
        ArrayList<JsonObject> moduleObjectList = new ArrayList<>();

        if (jsonObject.has("rule")) {
            JsonObject moduleObject = jsonObject.get("rule").getAsJsonObject();
            ArrayList<JsonObject> moduleRuleArray = handleModuleTypes(moduleObject);

            // Goes through all the modules in the arraylist.
            for (JsonObject o : moduleRuleArray) {
                if (o.has("moduleGroupId")) {
                    groupId = elementToString(o.get("moduleGroupId"));
                    JsonObject moduleObj = api.getModuleObjectFromApi(groupId);

                    String objType = moduleObj.get("type").toString();
                    if (objType.equals("\"StudyModule\"")) {
                        Module studyModule = createModule(moduleObj, groupId);
                        moduleObjectList.add(o);
                    }
                    else if (objType.equals("\"GroupingModule\"")) {
                        Module groupingModule = createModule(moduleObj, groupId);
                        moduleObjectList.add(o);
                    }
                    else {
                        System.out.println("Muu tyyppi kuin studyModule: " + objType);
                    }
                }
                else if (o.has("courseUnitGroupId")) {
                    groupId = elementToString(o.get("courseUnitGroupId"));
                    JsonObject moduleObj = api.getCourseUnitObjectFromApi(groupId);
                    Module courseUnitModule = createCourseUnitModule(moduleObj, groupId);
                    moduleObjectList.add(o);
                }
            }   
        } 
        return moduleObjectList;
    }
    
    /**
    * Handles recursively JsonObject modules which have different types. 
    * This function calls itself if the JsonObject's children needs to be handled. 
    * Function gathers all the selected modules as JsonObject type into ArrayList and returns it.
    * @param json JsonObject which type will be checked and 
    * @return ArrayList containing module JsonObjects.
    */
    public ArrayList<JsonObject> handleModuleTypes(JsonObject json) {
        Gson gson = new Gson();
        String moduleType = gson.toJson(json.get("type"));

        ArrayList<JsonObject> moduleRules = new ArrayList();
        if (moduleType.equals("\"CreditsRule\"")) {
            JsonObject newModule = json.get("rule").getAsJsonObject();
            moduleRules = handleModuleTypes(newModule);
        }
        else if (moduleType.equals("\"CompositeRule\"")) {
            JsonArray newModuleArray = json.get("rules").getAsJsonArray();
            moduleRules = handleJsonArray(newModuleArray);
            }
        else if (moduleType.equals("\"CourseUnitRule\"")) {
            moduleRules.add(json);
        }
        else if (moduleType.equals("\"AnyCourseUnitRule\"") || moduleType.equals("\"AnyUnitRule\"") 
            || moduleType.equals("\"AnyModuleRule\"")) {
            moduleRules.add(json);
        }
        else {
            // Muuta!
            System.out.println("no recognizable type for JsonObject: (handleModuleTypes)" + moduleType);
        }
        return moduleRules;
    }
        
    /**
    * Handles recursively JsonArray which contains different kind of moduleRule types.
    * Function adds all the selected modules as JsonObjects into ArrayList.
    * @param array JsonArray which elements will be handled.
    * @return Return ArrayList of module JsonObjects.
    */
    public ArrayList<JsonObject> handleJsonArray(JsonArray array) {
        Gson gson = new Gson();
        ArrayList<JsonObject> moduleRuleList = new ArrayList<>();
        for (JsonElement element : array) {
                String newModuleType = gson.toJson(element.getAsJsonObject().get("type"));
                
                if (newModuleType.equals("\"CompositeRule\"")) {
                    JsonArray newModuleArray = element.getAsJsonObject().get("rules").getAsJsonArray();
                    moduleRuleList = handleJsonArray(newModuleArray);     
                }
                else if (newModuleType.equals("\"ModuleRule\"") ||
                        newModuleType.equals("\"CreditsRule\"")) {
                    moduleRuleList.add(element.getAsJsonObject());
                }
                else if (newModuleType.equals("\"CourseUnitRule\"")) {
                    moduleRuleList.add(element.getAsJsonObject());    
                }
                else if (newModuleType.equals("\"AnyCourseUnitRule\"") || 
                        newModuleType.equals("\"AnyUnitRule\"") ||
                        newModuleType.equals("\"AnyModuleRule\"")) {
                    moduleRuleList.add(element.getAsJsonObject()); 
                }
                else {
                    System.out.println("Moduletype missing: "+ newModuleType);
                }
        }
        return moduleRuleList;
    }
    
    /**
    * Creates Module object using module's groupId information.
    * @param jsonObject JsonObject which data will be filtered and saved in Module object.
    * @param groupId GroupId identification number which will be save in Module object.
    * @return Module object which contains information about study or course.
    */
    public Module createModule(JsonObject jsonObject, String groupId) {
        String name; 
        String credits;
        String id;
        String type;
        
        type = elementToString(jsonObject.get("type"));
        id = elementToString(jsonObject.get("id"));
        
        if (type.equals("StudyModule" )) {
            JsonObject moduleNameObject = jsonObject.get("name").getAsJsonObject();
            if (moduleNameObject.has("fi")) {
                name = elementToString(moduleNameObject.get("fi"));
                }
            else if (moduleNameObject.has("en")) {
                name = elementToString(moduleNameObject.get("en"));
            }
            else {
                name = "";
            }
            
            JsonObject moduleCreditObject = jsonObject.get("targetCredits").getAsJsonObject();
            if (moduleCreditObject.has("min")) {
                credits = elementToString(moduleCreditObject.get("min"));
            }
            else {
                credits = "0";
            }
            Module studyModule = new Module(name,id,groupId,Integer.parseInt(credits),
                    STUDYMODULE);
            addDegreeModuleChildren(studyModule);
            return studyModule;
        }
        else if (type.equals("CourseUnit")) {
            System.out.println("TODO CourseUnit Module");
        }
        else if (type.equals("GroupingModule")) {
            JsonObject moduleNameObject = jsonObject.get("name").getAsJsonObject();
            if (moduleNameObject.has("fi")) {
                name = elementToString(moduleNameObject.get("fi"));
                }
            else if (moduleNameObject.has("en")) {
                name = elementToString(moduleNameObject.get("en"));
            }
            else {
                name = "";
            }
            credits = "0";
            Module studyModule = new Module(name,id,groupId,Integer.parseInt(credits),
                    GROUPINGMODULE);
            addDegreeModuleChildren(studyModule);
            return studyModule;
        }
        else {
            System.out.println("Type is missing from createModule: " + type);
        }
        return null;
    }
    
    /**
    * Creates Module object which is for courseUnit type module.
    * @param jsonObject JsonObject courseUnit module which data will be filtered and saved as Module.
    * @param groupId GroupId identification number which will be save in Module object.
    * @return Module object which has information about courseUnit type module.
    */
    public Module createCourseUnitModule(JsonObject jsonObject, String groupId) {
        String name; 
        String credits;
        String id;
        
        id = elementToString(jsonObject.get("id"));
        JsonObject moduleNameObject = jsonObject.get("name").getAsJsonObject();
        if (moduleNameObject.has("fi")) {
            name = elementToString(moduleNameObject.get("fi"));
            }
        else if (moduleNameObject.has("en")) {
            name = elementToString(moduleNameObject.get("en"));
        }
        else {
            name = "";
        }
        // ?? 
        JsonObject moduleCreditObject = jsonObject.get("credits").getAsJsonObject();
        if (moduleCreditObject.has("min")) {
            credits = elementToString(moduleCreditObject.get("min"));
        }
        else {
            // MinCredits are 0 if they are not determined.
            credits = "0";
        }
        Module courseUnitModule = new Module(name,id,groupId,Integer.parseInt(credits),
            COURSEUNITMODULE);
        addDegreeModuleChildren(courseUnitModule);
        return courseUnitModule;
    }
    
    /**
    * Creates Module object which is for anyCourse type module.
    * @return Module object which has information about anyCourse type module.
    */
    public Module createAnyCourseModule() {
        String name = ANYCOURSEUNIT_NAME;
        int credits = 0;
        String id = "";
        String groupId = "";
        Module anyModule = new Module(name,id,groupId,credits,ANYCOURSEUNITMODULE);
        addDegreeModuleChildren(anyModule);
        return anyModule;
    }
    
    /**
    * Adds children module list to Module object.
    * @param module Module which children modules will be added to it.
    */
    public void addDegreeModuleChildren(Module module) {
        String groupId = module.getGroupId();
        JsonObject moduleObj = null;
        if (module.getType() == COURSEUNITMODULE) {
            moduleObj = api.getCourseUnitObjectFromApi(groupId);
        }
        else {
            moduleObj = api.getModuleObjectFromApi(groupId);
        }
        ArrayList<JsonObject> childrenModules = getAllModules(moduleObj);
        // Goes through all the modules in the arraylist.
        for (JsonObject o : childrenModules) {
            if (o.has("moduleGroupId")) {
                // Type is module and there are more children to be added.
                groupId = elementToString(o.get("moduleGroupId"));
                JsonObject childrenObj = api.getModuleObjectFromApi(groupId);

                String objType = moduleObj.get("type").toString();
                if (objType.equals("\"StudyModule\"")) {
                    Module newStudyModule = createModule(childrenObj, groupId);
                    module.addChildren(newStudyModule);
                }
                else if (objType.equals("\"GroupingModule\"")) {
                    Module newModule = createModule(childrenObj, groupId);
                    module.addChildren(newModule);
                }
                else {
                    System.out.println("Muu tyyppi kuin studyModule: " + objType);
                }
            }
            else if (o.has("courseUnitGroupId")) {
                // If type is courseUnitModule.
                groupId = elementToString(o.get("courseUnitGroupId"));
                JsonObject childrenObj = api.getCourseUnitObjectFromApi(groupId);
                Module courseUnitModule = createCourseUnitModule(childrenObj, groupId);
                module.addChildren(courseUnitModule); 
            }
        }    
    } 

    /**
    * Creates ArrayList which contains all the degree names in alphabetical order.
    * @return ArrayList containing string type names of the degrees.
    */
    public ArrayList getDegreeNames() {
        ArrayList<String> degreeList = new ArrayList<>();
        for(String degreeName : degreeMap.keySet()) {
            degreeList.add(degreeName);
        }
        return degreeList;
    }
       
    /**
    * Returns ArrayList which contains selected degree's modules which have information about each course or studymodule.
    * @return ArrayList containing Degrees.Module type courses or studymodules.
    */
    ArrayList<Degrees.Module> getDegreeStructureList() {
        return this.degreeStructureList;
    }
    
    /**
    * Creates ArrayList which contains all the selected degree's possible major options.
    * @param degreeName Selected main degree which major information will be searched.
    * @return ArrayList containing Degrees.Major type objects which have information about each major.
    */
    ArrayList<Degrees.Major> getMajorList(String degreeName) {
        ArrayList<Degrees.Major> majorList = degreeMap.get(degreeName);
        return majorList;
    }
}
