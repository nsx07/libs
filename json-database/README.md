
   # JSON Database

   ## Overview

   This open-source Java project provides two essential components for managing data in your applications: a JSON-based database (`Database` class) and a repository  (`Repository` abstract class and `IRepository` interface). These components are designed to simplify data storage, retrieval, and management in your Java applications.

   ## JSON Database (Database Class)

   ### Features

   - Singleton design for managing a single database instance.
   - Dynamic configuration of the source file path.
   - Support for storing and retrieving entities of different types.
   - Exception handling for entities with null IDs.
   - Automatic table creation for entity types.

   ### Getting Started

   #### Prerequisites

   - Java 8 or higher
   - Gson library (included in the project)

   ## Initialization

   To get started with the JSON Database, initialize it and configure the source file path.

   ```java
   // Initialize database, source file path and pojo package
   Database.initialie("data.json", "Model");

   // Get the Database instance
   // Using Database directly its low-level and not very recommended
   // Only use if you know well how its works, most of the cases, use Repository
   Database<MyEntity> db = Database.getInstance();
   ```

   ### Entity Configuration
   Configure the entity type before using the database.


   ```java
   db.configureType(MyEntity.class);
   ```
   ### Storing Entities
   You can store entities individually or in a list.

   ```java
   MyEntity entity = new MyEntity();
   entity.setId(UUID.randomUUID());

   try {
      db.set(entity);
   } catch (NullIdException e) {
      // Handle null ID exception
   }
   ```
   ```java
   List<MyEntity> entities = new ArrayList<>();
   // Add entities to the list

   try {
      db.set(entities);
   } catch (NullIdException e) {
      // Handle null ID exception
   }
   ```
   ### Retrieving Entities
   Retrieve entities from the database using the get() method.

   ```java
   Stream<MyEntity> entityStream = db.get();
   // You can further process the stream to work with the retrieved entities.
   ```
   ### Documentation
   For detailed documentation of the Database class and its methods, please refer to the Javadoc comments in the source code.

   ## Repository  (Repository Abstract Class and IRepository Interface)
   ### Features
   * Abstract base class for implementing a generic repository.
   * Common interface for saving, retrieving, and deleting entities.
   * Flexible design to support various entity types and (@soon primary key types).

   ### Usage
   1. #### Create Entity Classes
      Define your entity classes that implement the EntityBase abstract class. These entities represent the data you want to store and retrieve.

   ```java

   public class MyEntity extends EntityBase {
      // Add entity-specific fields and methods here
   }
   ```

   ### Implement a Concrete Repository
   Create a concrete repository class that extends the Repository abstract class. This class should provide the actual data storage and retrieval logic.

   ```java
   public class MyEntityRepository extends Repository<MyEntity> 
   ```

   ### Usage in Application
   In your application code, you can use the concrete repository to save, retrieve, and delete entities.

   ```java
   MyEntityRepository repository = new MyEntityRepository();

   // Save an entity
   MyEntity entity = new MyEntity();
   UUID entityId = repository.save(entity);

   // Retrieve an entity by ID
   MyEntity retrievedEntity = repository.getById(entityId);

   // Retrieve all entities
   List<MyEntity> allEntities = repository.getAll();

   // Delete an entity by ID
   boolean isDeleted = repository.deleteById(entityId);
   ```

   ### Documentation
   For detailed documentation of the Repository abstract class and the IRepository interface, please refer to the Javadoc comments in the source code.

   ## Reporting Issues
   This project is open-source, and we welcome contributions and issue reports. If you encounter any problems, have questions, or want to contribute to the project, please open an issue on the GitHub repository so that our community can assist you.

   ## License
   This project is licensed under the MIT License - see the LICENSE file for details.

   ## Acknowledgments
   This project is inspired by common design patterns and best practices for data management in Java applications.
   Special thanks to the Gson library for simplifying JSON serialization and deserialization.