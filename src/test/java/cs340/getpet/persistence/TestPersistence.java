package cs340.getpet.persistence;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cs340.getpet.persistence.Persistence.PersistenceException;

public class TestPersistence {
    private static final Animal[] testingAnimals = new Animal[] {
        new Animal.Builder()
                .cageNumber(5)
                .species(Species.DOG)
                .breed("Shiba Inu")
                .size(Size.MEDIUM)
                .colors(new Color[] { Color.GOLD })
                .gender(Gender.MALE)
                .weight(42.0)
                .vaccinated(true)
                .spayNeuter(true)
                .name("Doge")
                .missing(false)
                .build(),
        new Animal.Builder()
                .cageNumber(6)
                .species(Species.CAT)
                .breed("Tabby")
                .size(Size.SMALL)
                .colors(new Color[] { Color.GOLD, Color.WHITE })
                .gender(Gender.MALE)
                .weight(22.0)
                .vaccinated(true)
                .spayNeuter(true)
                .name("Garfield")
                .missing(false)
                .build(),
    };

    @Test
    public void testAddAnimal() throws PersistenceException {
        Persistence persistence = new Persistence(":memory:");

        int intakeNumber = persistence.newAnimal(testingAnimals[0]);
        Assertions.assertEquals(testingAnimals[0], persistence.getAnimal(intakeNumber));
    }

    // Here are some ideas for tests:
    // Persistence.search:
    //      - Make a test that searches with an empty query, assert that it has all of the sample animals (Found in src/resources/sample-db-create.sql)
    //      - Make a test that searches with a very specific query, assert that is has the expected sample animal
    //      - Make a test that searches with a typical user query, assert that it has the expected sample animal(s)
    //      - Make a test that adds an animal and searches for it immediately after with a precise query
    // Persistence.newAnimal:
    //      - Make a test that adds the same animal twice, assert that the two intake numbers returned back are different
    //      - Make a test that adds an animal and deletes it immediately, making sure that it is deleted properly
    //      - Make a test that gets an animal and adds it to the database a second time
    // Persistence.deleteAnimal:
    //      - Make a test that adds an animal, updates it, then deletes it

    @Test
    public void testDeleteAnimal() throws PersistenceException {
        Persistence persistence = new Persistence(":memory:");

        final int INTAKE_NUMBER = 1;

        Animal preexistingDoge = persistence.getAnimal(INTAKE_NUMBER);
        Assertions.assertNotNull(preexistingDoge);
        
        persistence.deleteAnimal(INTAKE_NUMBER);

        Animal doge = persistence.getAnimal(INTAKE_NUMBER);
        Assertions.assertNull(doge);
    }

    @Test
    public void testDeleteTwice() throws PersistenceException {
        Persistence persistence = new Persistence(":memory:");

        final int PREEXISTING_INTAKE_NUMBER = 1;

        // Assert that the animal already exists
        Assertions.assertNotNull(persistence.getAnimal(PREEXISTING_INTAKE_NUMBER));

        // Delete the animal
        Assertions.assertTrue(persistence.deleteAnimal(PREEXISTING_INTAKE_NUMBER));

        // Assert that it no longer exists
        Assertions.assertNull(persistence.getAnimal(PREEXISTING_INTAKE_NUMBER));

        // Delete a second time
        Assertions.assertFalse(persistence.deleteAnimal(PREEXISTING_INTAKE_NUMBER));

        // Assert that is still does not exist
        Assertions.assertNull(persistence.getAnimal(PREEXISTING_INTAKE_NUMBER));
    }

    @Test
    public void testDeleteNonexistent() throws PersistenceException {
        Persistence persistence = new Persistence(":memory:");

        final int NONEXISTENT_INTAKE_NUMBER = 20;

        // Assert that the animal does not already exist
        Assertions.assertNull(persistence.getAnimal(NONEXISTENT_INTAKE_NUMBER));

        // Try to delete it
        Assertions.assertFalse(persistence.deleteAnimal(NONEXISTENT_INTAKE_NUMBER));

        // Assert that it still does not exist
        Assertions.assertNull(persistence.getAnimal(NONEXISTENT_INTAKE_NUMBER));
    }

    @Test
    public void testDeletePreexisting() throws PersistenceException {
        Persistence persistence = new Persistence(":memory:");

        final int PREEXISTING_INTAKE_NUMBER = 1;

        // Assert that the animal already exists
        Assertions.assertNotNull(persistence.getAnimal(PREEXISTING_INTAKE_NUMBER));

        // Delete the animal
        Assertions.assertTrue(persistence.deleteAnimal(PREEXISTING_INTAKE_NUMBER));

        // Assert that it no longer exists
        Assertions.assertNull(persistence.getAnimal(PREEXISTING_INTAKE_NUMBER));
    }
	
	@Test
	public void testEmptyQuery() throws PersistenceException{
		 Persistence persistence = new Persistence(":memory:");
		// Empty Query
		SearchQuery empty = new SearchQuery(null, null, null, null, null ,null , false, false);
		
		Assertions.assertEquals(persistence.search(empty).length, 9);
	}
	
	@Test
	public void testDuplicate() throws PersistenceException {
		 Persistence persistence = new Persistence(":memory:");
		 int animal = persistence.newAnimal(testingAnimals[0]);
		 int duplicate = persistence.newAnimal(testingAnimals[0]);
		 Assertions.assertNotEquals(duplicate, animal);
	}
	
	@Test
	public void testAddDelete() throws PersistenceException {
		 Persistence persistence = new Persistence(":memory:");
		 int animal = persistence.newAnimal(testingAnimals[0]);
		 Assertions.assertTrue(persistence.deleteAnimal(animal));
		 Assertions.assertNull(persistence.getAnimal(animal));
	}
	
	@Test
	public void testAddPreexisting() throws PersistenceException {
		 Persistence persistence = new Persistence(":memory:");
		 int animal = persistence.newAnimal(persistence.getAnimal(1));
         Assertions.assertEquals(persistence.getAnimal(1), persistence.getAnimal(animal));
	}
	
	@Test
	public void testSpecific() throws PersistenceException {
		Persistence persistence = new Persistence(":memory:");
		SearchQuery animal = new SearchQuery(Species.DOG, new Gender[] { Gender.MALE}, "Shiba Inu", new Color[] { Color.GOLD }, new Size[] { Size.MEDIUM }, 2, true, true);
		Assertions.assertEquals(persistence.search(animal).length, 1);
	}
	
	@Test
	public void testAddAnimalTwice() throws PersistenceException {
		Persistence persistence = new Persistence(":memory:");
		int intakeOne = persistence.newAnimal(testingAnimals[0]);
		int intakeTwo = persistence.newAnimal(testingAnimals[0]);
		Assertions.assertNotEquals(intakeOne, intakeTwo);
	}
	
	@Test
	public void testAddAnimalThenDelete() throws PersistenceException {
		Persistence persistence = new Persistence(":memory:");
		int intakeOne = persistence.newAnimal(testingAnimals[0]);
		persistence.deleteAnimal(intakeOne);
		Assertions.assertNull(persistence.getAnimal(intakeOne));
	}
	
	@Test
	public void testAddAnimalSecondTime() throws PersistenceException {
		Persistence persistence = new Persistence(":memory:");
		Assertions.assertNotEquals(persistence.newAnimal(persistence.getAnimal(1)),1);
	}
	
    @Test
    public void testAddUpdateDelete() throws PersistenceException {
        Persistence persistence = new Persistence(":memory:");
        
        int intakeNumber = persistence.newAnimal(testingAnimals[0]);

        Assertions.assertTrue(persistence.updateAnimal(intakeNumber, testingAnimals[1]));

        Assertions.assertNotEquals(testingAnimals[0], persistence.getAnimal(intakeNumber));
        Assertions.assertEquals(testingAnimals[1], persistence.getAnimal(intakeNumber));

        Assertions.assertTrue(persistence.deleteAnimal(intakeNumber));

        Assertions.assertNull(persistence.getAnimal(intakeNumber));
    }

    @Test
    public void testGetCages() throws PersistenceException {
        Persistence persistence = new Persistence(":memory:");

        Cage[] expected = new Cage[] {
            new Cage(0, 1, 0),
            new Cage(1, 0, 1),
            new Cage(2, 2, 2),
            new Cage(8, 1, 0),
            new Cage(9, 1, 0),
            new Cage(13, 1, 0)
        };

        Cage[] actual = persistence.getCages();

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testGetCagesNewAnimal() throws PersistenceException {
        Persistence persistence = new Persistence(":memory:");

        persistence.newAnimal(testingAnimals[1]);

        Cage[] expected = new Cage[] {
            new Cage(0, 1, 0),
            new Cage(1, 0, 1),
            new Cage(2, 2, 2),
            new Cage(6, 0, 1),
            new Cage(8, 1, 0),
            new Cage(9, 1, 0),
            new Cage(13, 1, 0)
        };

        Cage[] actual = persistence.getCages();

        Assertions.assertArrayEquals(expected, actual);
    }
}
