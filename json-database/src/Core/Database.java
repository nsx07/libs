package Core;

import Utils.CoreUtils;
import Utils.NullIdException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Represents a generic database for storing and retrieving entities of a specified type.
 * @param <T> The type of entities to be stored in the database.
 */
@SuppressWarnings("all")
public class Database<T> {

    private static CoreUtils coreUtils = new CoreUtils();
    private Class<T> entityInstance = null;
    private final Gson gson = new Gson();
    private static Database instance;
    private static String filePath;
    private String tableName;

    private Database() {}

    /**
     * Gets the singleton instance of the Database.
     * @param <T> The type of entities to be stored in the database.
     * @return The Database instance.
     */
    public static synchronized <T> Database getInstance() {
        if (instance == null) {
            instance = new Database<T>();
        } return instance;
    }

    /**
     * Configures the source file for the database.
     * @param sourceFile The path to the JSON source file.
     * @throws IOException If an I/O error occurs.
     */    
    public static void configureSourceFile(String sourceFile) throws IOException {
        filePath = sourceFile;
        initialize();
    }

    /**
     * Configures the entity type for the database.
     * @param instance The class representing the entity type.
     */
    public void configureType(Class<T> instance) {
        entityInstance = instance;
        var classFullName = entityInstance.getTypeName().split("\\.");
        this.tableName = classFullName[classFullName.length - 1].toLowerCase();
    }

    /**
     * Sets a list of entities in the database.
     * @param array The list of entities to set.
     * @throws NullIdException If an entity with a null ID is encountered.
     */
    public void set(List<T> array) throws NullIdException {
        for (var each: array) {
            if (((EntityBase) each).getId() == null) {
                throw new NullIdException();
            }
        }

        writeJSON(array, false);
    }

    /**
     * Adds an entity to the database.
     * @param entity The entity to add.
     * @throws NullIdException If the entity has a null ID.
     */
    public void set(T entity) throws NullIdException {
        boolean newTable = false;
        List<Map<String, Object>> json = readJSON();
        List<T> entities = segmentJson(json);

        if (entities == null) {
            newTable = true;
            entities = new ArrayList<>();
        }

        entities.add(entity);

        for (var each: entities) {
            if (((EntityBase) each).getId() == null) {
                throw new NullIdException();
            }
        }

        writeJSON(entities, newTable);
    }

    /**
     * Retrieves entities from the database.
     * @return A stream of entities.
     */
    public Stream<T> get() {
        List<Map<String, Object>> json = readJSON();

        if (json == null) {
            return Stream.of();
        }

        return segmentJson(json).stream();
    }

    private List<T> segmentJson(List<Map<String, Object>> jsonList) {
        List<T> data = new ArrayList<>();

        for (Map<String, ?> json : jsonList ) {
            if (json.get("table").toString().equals(this.tableName) && json.get("data") != null) {
                Type type = TypeToken.getParameterized(List.class, entityInstance).getType();
                data = gson.fromJson(gson.toJson(json.get("data")), type);
            }
        }

        return data;
    }

    private List<Map<String, Object>> readJSON() {
        List<Map<String, Object>> json = new ArrayList<>();
        try (Reader reader = new FileReader(filePath)) {
            Type type = new TypeToken<List<Map<String, ?>>>() {}.getType();
            json = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void writeJSON(List<T> data, boolean isNew) {
        var json = readJSON();

        if (!isNew) {
            json.stream().forEach(x -> {
                if (x.get("table").equals(tableName)) {
                    x.put("data", data);
                }
            });
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("table", tableName);
            map.put("data", data);
            json.add(map);
        }

        Gson gson = new GsonBuilder().create();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the database by checking and creating tables for entity types.
     * @throws IOException If an I/O error occurs.
     */
    private static void initialize() throws IOException {

        Database db = Database.getInstance();
        List<String> entitiesReady = new ArrayList<>();
        List<String> entitiesToAdd = new ArrayList<>();
        List<Map<String, Object>> json = db.readJSON();
        
        if (json != null) {
            json.forEach(x -> entitiesReady.add(x.get("table").toString()));
        }

        ArrayList<Class<?>> classes;
        classes = coreUtils.getClasses("Core");
        List<String> entities = new ArrayList<>();
        
        classes.forEach(x -> {
            if (x.getSuperclass() != null && x.getSuperclass().getSimpleName().equals("EntityBase")) {
                entities.add(x.getSimpleName());
            }
        });
        
        entities.forEach(x -> {
            if (!entitiesReady.contains(x.toLowerCase())) {
                entitiesToAdd.add(x);
            }
        });

        entitiesToAdd.forEach(x -> {
            Map<String, Object> map = new HashMap<>();
            map.put("table", x.toLowerCase());
            map.put("data", new ArrayList<>());
            json.add(map);
        });

        Gson gson = new GsonBuilder().create();
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
