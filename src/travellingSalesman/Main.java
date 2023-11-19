package travellingSalesman;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.lang.Math;

public class Main {
	static final String FILE_NAME = "test.txt"; // Change this to change the file used 
	static final String[] LOADING_CHARACTERS = {"|\r", "/\r", "―\r", "\\\r"};
	static final int[][] CITIES_ARRAY = getCities();
	static final int CITIES_LENGTH = CITIES_ARRAY.length;
	static final int POP_SIZE = CITIES_LENGTH * 10;
	static final int BEST_SIZE = (int)CITIES_LENGTH/2;
	static final int CANDIDATES_SIZE = CITIES_LENGTH * 4;
 	public static int[][] getCities() { // Reads the file and creates an array of cities in order
		int[][] cArray = null;
		try {
			String dataFile = System.getProperty("user.dir") + File.separator + FILE_NAME;
			String text = "";
	        File myObj = new File(dataFile);
	        Scanner myReader = new Scanner(myObj);
	        while (myReader.hasNextLine()) { // Read all lines and add them to the string
	            String data = myReader.nextLine();
	            text += data.trim() + "\n";
	        }
	        String[] textArray = text.split("\n"); // Split the string by lines
	        cArray = new int[textArray.length][3];
	        for (int cityIndex = 0; cityIndex < textArray.length; cityIndex++) { // Split the lines by whitespaces (spaces, tabs, etc.) and convert them to integers
	        	cArray[cityIndex][0] = Integer.parseInt(textArray[cityIndex].split("\\s+")[0]);
	        	cArray[cityIndex][1] = Integer.parseInt(textArray[cityIndex].split("\\s+")[1]);
	        	cArray[cityIndex][2] = Integer.parseInt(textArray[cityIndex].split("\\s+")[2]);
	        }
	        myReader.close();
		}
		catch (FileNotFoundException e) {
            System.out.println("File " + FILE_NAME + " not found.");
            e.printStackTrace();
        }
        return cArray;
	}
	
	public static double getDistance(int[][] cArray) { // Calculates total distance of a route
		double distance = 0;
		for (int cityIndex = 0; cityIndex < cArray.length; cityIndex++) {
			if (cityIndex != cArray.length - 1) { // Checks if iterator has reached the last index
				distance += Math.sqrt(Math.pow(cArray[cityIndex + 1][1] - cArray[cityIndex][1], 2) + Math.pow(cArray[cityIndex + 1][2] - cArray[cityIndex][2], 2));
			}
			else {
				distance += Math.sqrt(Math.pow(cArray[0][1] - cArray[cityIndex][1], 2) + Math.pow(cArray[0][2] - cArray[cityIndex][2], 2));
			}
		}
		return distance;
	}
	
	public static int[][] mutateArray(int[][] cArray) { // Scrambles the array containing a route
		int[][] mutatedArray = cArray;
		int counter = 1 + (int)(Math.random() * (mutatedArray.length * 2)); // Amount of times two random cities will be swapped
		while (counter > 0) {
			int randIndex1 = 1 + (int)(Math.random() * (mutatedArray.length - 1));  
			int randIndex2 = 1 + (int)(Math.random() * (mutatedArray.length - 1));
			while (randIndex1 == randIndex2) {
				randIndex2 = 1 + (int)(Math.random() * (mutatedArray.length - 1));
			}
			int[] copy = mutatedArray[randIndex1];
			mutatedArray[randIndex1] = mutatedArray[randIndex2];
			mutatedArray[randIndex2] = copy;
			counter--;
		}
		return mutatedArray;
	}
	
	public static int[][] getPath(int[][][] bestArray, int counter) { // Returns a candidate for the optimal path
		System.out.print("Calculating path " + LOADING_CHARACTERS[counter % 4]); 
		if (counter == POP_SIZE * 10) { // Return the current best path if no better candidate is found within POP_SIZE * 10 generations of finding it
			return bestArray[0];
		}
		else {
			int[][][] gArray = new int[POP_SIZE][CITIES_LENGTH][3];
			int newCounter = counter;
			for (int pathIndex = 0; pathIndex < gArray.length; pathIndex++) {
				int randIndex = (int)(Math.random() * (bestArray.length - 1));
				for (int cityIndex = 0; cityIndex < gArray[0].length; cityIndex++) {
					gArray[pathIndex][cityIndex][0] = bestArray[randIndex][cityIndex][0];
					gArray[pathIndex][cityIndex][1] = bestArray[randIndex][cityIndex][1];
					gArray[pathIndex][cityIndex][2] = bestArray[randIndex][cityIndex][2];
				}
				mutateArray(gArray[pathIndex]);
			}
			Arrays.sort(gArray, (a, b) -> Double.compare(getDistance(a), getDistance(b)));
			int[][][] newBestArray = new int[bestArray.length][CITIES_LENGTH][3];
			for (int pathIndex = 0; pathIndex < bestArray.length; pathIndex++) {
				for (int cityIndex = 0; cityIndex < gArray[0].length; cityIndex++) {
					newBestArray[pathIndex][cityIndex][0] = gArray[pathIndex][cityIndex][0];
					newBestArray[pathIndex][cityIndex][1] = gArray[pathIndex][cityIndex][1];
					newBestArray[pathIndex][cityIndex][2] = gArray[pathIndex][cityIndex][2];
				}
			}
			if (getDistance(newBestArray[0]) >= getDistance(bestArray[0])) { // Save the best path from the previous generation if it's shorter than the best path from the new generation
				for (int cityIndex = 0; cityIndex < gArray[0].length; cityIndex++) {
					newBestArray[0][cityIndex][0] = bestArray[0][cityIndex][0];
					newBestArray[0][cityIndex][1] = bestArray[0][cityIndex][1];
					newBestArray[0][cityIndex][2] = bestArray[0][cityIndex][2];
				}
				newCounter++; // And increment the counter
			}
			else { // Otherwise, reset the counter to 0
				newCounter = 0;
			}
			return getPath(newBestArray, newCounter);
		}
	}
	
	public static void printPath(int[][] pathArray) { // Prints a path as a sequence of cities
		for (int cityIndex = 0; cityIndex < pathArray.length; cityIndex++) {
			if(cityIndex < pathArray.length - 1) {
				System.out.print(pathArray[cityIndex][0] + " => ");
			}
			else {
				System.out.print(pathArray[cityIndex][0] + " => " + pathArray[0][0] + "\n");
			}
		}
	}
	
	public static int[][][] getInitialPop() { // Generates an array of random paths
		int[][][] gArray = new int[POP_SIZE][CITIES_LENGTH][3];
		for (int pathIndex = 0; pathIndex < gArray.length; pathIndex++) { 
			for (int cityIndex = 0; cityIndex < CITIES_LENGTH; cityIndex++) {
				gArray[pathIndex][cityIndex][0] = CITIES_ARRAY[cityIndex][0]; // Can't do generationArray[i][j] = CITIES_ARRAY[j] because of how assigning variables in Java works
				gArray[pathIndex][cityIndex][1] = CITIES_ARRAY[cityIndex][1]; // generationArray[i][j] = CITIES_ARRAY[j] assigns a reference to CITIES_ARRAY[j], not its value
				gArray[pathIndex][cityIndex][2] = CITIES_ARRAY[cityIndex][2]; 
			}
			mutateArray(gArray[pathIndex]); // Scrambles the array
		}
		return gArray;
	}
	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		int[][][] initialPopArray = getInitialPop(); // Generate initial population
		Arrays.sort(initialPopArray, (a, b) -> Double.compare(getDistance(a), getDistance(b)));
		int[][][] bestArray = new int[BEST_SIZE][CITIES_LENGTH][3];
		for (int pathIndex = 0; pathIndex < bestArray.length; pathIndex++) {
			for (int cityIndex = 0; cityIndex < initialPopArray[0].length; cityIndex++) {
				bestArray[pathIndex][cityIndex][0] = initialPopArray[pathIndex][cityIndex][0];
				bestArray[pathIndex][cityIndex][1] = initialPopArray[pathIndex][cityIndex][1];
				bestArray[pathIndex][cityIndex][2] = initialPopArray[pathIndex][cityIndex][2];
			}
		}
		int[][][] candidateArray = new int[CANDIDATES_SIZE][CITIES_LENGTH][3];
		for (int pathIndex = 0; pathIndex < candidateArray.length; pathIndex++) {  // Generate a number of candidates for optimal solution, sort and pick the best one
			int[][] path = getPath(bestArray, 0);
			for (int cityIndex = 0; cityIndex < candidateArray[pathIndex].length; cityIndex++) {
				candidateArray[pathIndex][cityIndex][0] = path[cityIndex][0];
				candidateArray[pathIndex][cityIndex][1] = path[cityIndex][1];
				candidateArray[pathIndex][cityIndex][2] = path[cityIndex][2];
			}
		}
		Arrays.sort(candidateArray, (a, b) -> Double.compare(getDistance(a), getDistance(b)));
		long finishTime = System.nanoTime();
		java.awt.Toolkit.getDefaultToolkit().beep(); // Emit a system beep upon completion
		System.out.println("Calculated in " + (double)(finishTime - startTime) / 1000000000 + " seconds");
		printPath(candidateArray[0]);
		System.out.println("Distance: " + getDistance(candidateArray[0]));
	}
}