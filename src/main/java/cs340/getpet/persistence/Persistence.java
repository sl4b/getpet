package cs340.getpet.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import cs340.getpet.util.EnumSerializer;

// NOTE: this class cannot be multithreaded using a single connection, as it would have race conditions.
public class Persistence {
    Connection conn;

    public static class PersistenceException extends Exception {
        PersistenceException(String message) {
            super(message);
        }

        PersistenceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Creates a Persistence, connecting to the database using the given information.
     *
     * @param database the database filename to give to SQLite, or ":memory:" for an in-memory DB
     * @throws PersistenceException when the database connection could not be created
     */
    public Persistence(String database) throws PersistenceException {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create database connection", e);
        }
        
        try {
            applySampleData();
        } catch (SQLException | IOException e) {
            throw new PersistenceException("Failed to apply sample data", e);
        }
    }

    /**
     * Applies sample data to the database.
     * 
     * @throws SQLException when the application fails
     * @throws IOException when the sample data cannot be read
     */
    private void applySampleData() throws SQLException, IOException {
        Statement batch = conn.createStatement();
        String sql = new String(getClass().getResourceAsStream("/sample-db-create.sql").readAllBytes(), StandardCharsets.UTF_8);
        for (String stmt : sql.split(";")) {
            batch.addBatch(stmt.trim());
        }
        batch.executeBatch();
    }

    /**
     * Retrieves information about the cages contained in the database.
     * 
     * @return The cages in the database
     * @throws PersistenceException when the database query fails
     */
    public Cage[] getCages() throws PersistenceException {
        try (Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery("SELECT cageNumber, species, COUNT(*) as count FROM Animals GROUP BY cageNumber, species");

            Stack<Cage> cages = new Stack<>();
            while (resultSet.next()) {
                int cageNumber = resultSet.getInt("cageNumber");
                Species species = EnumSerializer.fromString(resultSet.getString("species"), Species.class);
                
                int dogCount;
                int catCount;
                if (species == Species.DOG) {
                    dogCount = resultSet.getInt("count");
                    if (!cages.isEmpty() && cageNumber == cages.peek().cageNumber)
                        catCount = cages.pop().catCount;
                    else
                        catCount = 0;
                } else {
                    catCount = resultSet.getInt("count");
                    if (!cages.isEmpty() && cageNumber == cages.peek().cageNumber)
                        dogCount = cages.pop().dogCount;
                    else
                        dogCount = 0;
                }

                cages.push(new Cage(cageNumber, dogCount, catCount));
            }

            return cages.toArray(new Cage[0]);
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create or execute query to search for cages", e);
        }
    }

    /**
     * Retrieves an animal fron the database using its intake number as a key.
     * 
     * @param intakeNumber the intake number of the animal
     * @return the found animal, or null if none is found
     * @throws PersistenceException when the database query fails
     */
    public Animal getAnimal(int intakeNumber) throws PersistenceException {
        String queryString = "SELECT * FROM Animals WHERE intakeNumber = ?";

        try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
            stmt.setInt(1, intakeNumber);

            ResultSet resultSet = stmt.executeQuery();

            resultSet.next();

            if (!resultSet.isAfterLast())
                return animalFromRow(resultSet);
            else
                return null;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create or execute animal search statement", e);
        }
    }

    /**
     * Searches for animals in the database using a SearchQuery.
     * 
     * @param searchRequest the search query
     * @return the list of found animals
     * @throws PersistenceException when the database query fails
     */
    public Animal[] search(SearchQuery searchRequest) throws PersistenceException {
        // LinkedLists used to build the where clause of the query
        final LinkedList<String> ands = new LinkedList<>();
        ands.push("TRUE");
        final LinkedList<Object> parameters = new LinkedList<>();

        // Functions used in conjunction with the previously defined variables
        // to build the where clause in an easy-to-understand way.
        final BiConsumer<String, Object> is = (attrName, value) -> {
            if (value != null) {
                // add condition text
                ands.add(attrName + " = ?");
                // add parameters
                parameters.add(value);
            }
        };
        final BiConsumer<String, String> like = (attrName, value) -> {
            if (value != null) {
                // add condition text
                ands.add(attrName + " LIKE ?");
                // add parameters
                parameters.add("%" + value + "%");
            }
        };
        final BiConsumer<String, Object[]> in = (attrName, values) -> {
            if (values != null && values.length != 0) {
                // add condition text
                ands.add(attrName + " IN (" + "?,".repeat(values.length - 1) + "?)");
                // add parameters
                parameters.addAll(Arrays.asList(values));
            }
        };
        final BiConsumer<String, String[]> has = (attrName, values) -> {
            if (values != null && values.length != 0) {
                // add condition text
                StringBuilder sb = new StringBuilder("(");
                for (int i = 0; i < values.length - 1; ++i)
                    sb.append(attrName).append(" LIKE ? OR ");
                sb.append(attrName).append(" LIKE ?");
                ands.add(sb.append(')').toString());
                // add parameters
                for (String value : values)
                    parameters.add("%" + value + "%");
            }
        };

        if (searchRequest.species != null)
            is.accept("species", searchRequest.species.toString());
        if (searchRequest.genders != null)
            in.accept("gender", Arrays.stream(searchRequest.genders).map(gender -> gender.toString()).collect(Collectors.toList()).toArray(new String[0]));
        if (searchRequest.breed != null)
            like.accept("breed", searchRequest.breed);
        if (searchRequest.colors != null)
            has.accept("color", Arrays.stream(searchRequest.colors).map(color -> color.toString()).collect(Collectors.toList()).toArray(new String[0]));
        if (searchRequest.sizes != null)
            in.accept("size", Arrays.stream(searchRequest.sizes).map(size -> size.toString()).collect(Collectors.toList()).toArray(new String[0]));
        if (searchRequest.cageNumber != null)
            is.accept("cageNumber", searchRequest.cageNumber);
        if (searchRequest.vaccinated)
            is.accept("vaccinated", 1);
        if (searchRequest.spayNeuter)
            is.accept("spayNeuter", 1);

        String queryString = "SELECT * FROM Animals WHERE "
                + String.join(" AND ", ands)
                + " ORDER BY name";

        try (PreparedStatement stmt = conn.prepareStatement(queryString)) {
            // set parameters
            int i = 1;
            for (Object parameter : parameters)
                stmt.setObject(i++, parameter);

            // execute query
            ResultSet resultSet = stmt.executeQuery();
            
            // put results into usable format
            ArrayList<Animal> results = new ArrayList<>();
            while (resultSet.next())
                results.add(animalFromRow(resultSet));

            return results.toArray(new Animal[0]);
        } catch (SQLException e) {
            throw new PersistenceException("Failed to create or execute animal search statement", e);
        }
    }

    /**
     * Adds an animal to the database, automatically assigning it an intake number.
     * 
     * @param animal the animal to add to the database
     * @throws PersistenceException when the database update fails
     * @return the automatically assigned intake number of the animal
     */
    public int newAnimal(Animal animal) throws PersistenceException {
        String query = "INSERT INTO Animals (species,vaccinated,breed,gender,name,color,weight,cageNumber,ownerCustomerId,missing,spayNeuter,size) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] parameters = new Object[] {
                animal.species.toString(),
                animal.vaccinated ? 1 : 0,
                animal.breed,
                animal.gender.toString(),
                animal.name,
                String.join(",", Arrays.stream(animal.colors).map(Color::toString).collect(Collectors.toList())),
                animal.weight,
                animal.cageNumber,
                null,
                animal.missing ? 1 : 0,
                animal.spayNeuter ? 1 : 0,
                animal.size.toString(),
        };

        try (PreparedStatement prepStmt = conn.prepareStatement(query)) {
            // make sure we're setting the right number of parameters as a sanity check
            assert prepStmt.getParameterMetaData().getParameterCount() == parameters.length;

            for (int i = 0; i < parameters.length; ++i)
                prepStmt.setObject(i + 1, parameters[i]);

            if (prepStmt.executeUpdate() != 1)
                ; // this is bad D:
            
            // get the automatically-assigned intake number of the animal
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT last_insert_rowid() AS intakeNumber");
            resultSet.next();
            int intakeNumber = resultSet.getInt("intakeNumber");

            return intakeNumber;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to update animal", e);
        }
    }

    /**
     * Updates an animal in the database using an intake number as the key.
     * 
     * @param intakeNumber the intake number of the animal to update
     * @param animal the new animal data to be stored in the corresponding row of the database table
     * @return whether or not an animal was updated
     * @throws PersistenceException
     */
    public boolean updateAnimal(int intakeNumber, Animal animal) throws PersistenceException {
        String query = "UPDATE Animals SET " +
                "species = ?," +
                "breed = ?," +
                "size = ?," +
                "color = ?," +
                "gender = ?," +
                "weight = ?," +
                "vaccinated = ?," +
                "spayNeuter = ?," +
                "name = ?," +
                "missing = ?," +
                "cageNumber = ? " +
                "WHERE intakeNumber = ?";
        Object[] parameters = new Object[] {
                animal.species.toString(),
                animal.breed,
                animal.size.toString(),
                String.join(",", Arrays.stream(animal.colors).map(Color::toString).collect(Collectors.toList())),
                animal.gender.toString(),
                animal.weight,
                animal.vaccinated ? 1 : 0,
                animal.spayNeuter ? 1 : 0,
                animal.name,
                animal.missing ? 1 : 0,
                animal.cageNumber,
                intakeNumber,
        };

        try (PreparedStatement prepStmt = conn.prepareStatement(query)) {
            // make sure we're setting the right number of parameters as a sanity check
            assert prepStmt.getParameterMetaData().getParameterCount() == parameters.length;

            for (int i = 0; i < parameters.length; ++i)
                prepStmt.setObject(i + 1, parameters[i]);

            return prepStmt.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to update animal", e);
        }
    }

    /**
     * Removes an animal from the database, using its intake number as a key.
     * 
     * @param intakeNumber the intake number of the animal to remove
     * @throws PersistenceException when the database operation fails
     * @return whether or not an animal was deleted
     */
    public boolean deleteAnimal(int intakeNumber) throws PersistenceException {
        String query = "DELETE FROM Animals WHERE intakeNumber = ?";

        try (PreparedStatement prepStmt = conn.prepareStatement(query)) {
            prepStmt.setInt(1, intakeNumber);

            return prepStmt.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new PersistenceException("Failed to euthanize animal", e);
        }
    }

    /**
     * Constructs an animal from a row of the Animals table.
     * 
     * @param resultSet the result set, positioned at the row that data should be retrieved from
     * @return the constructed animal
     * @throws SQLException when a field could not be found
     */
    private static Animal animalFromRow(ResultSet resultSet) throws SQLException {
        return new Animal.Builder()
                .intakeNumber(resultSet.getInt("intakeNumber"))
                .cageNumber(resultSet.getInt("cageNumber"))
                .species(Species.fromString(resultSet.getString("species")))
                .breed(resultSet.getString("breed"))
                .size(Size.fromString(resultSet.getString("size")))
                .colors(Arrays.stream(resultSet.getString("color").split(","))
                        .map(Color::fromString)
                        .toArray(Color[]::new))
                .gender(Gender.fromString(resultSet.getString("gender")))
                .weight(resultSet.getDouble("weight"))
                .vaccinated(resultSet.getInt("vaccinated") != 0)
                .spayNeuter(resultSet.getInt("spayNeuter") != 0)
                .name(resultSet.getString("name"))
                .missing(resultSet.getInt("missing") != 0)
                .build();
    }
}
