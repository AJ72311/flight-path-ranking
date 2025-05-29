import java.util.Scanner;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        // adjacency list
        LinkedList<City> adjList = new LinkedList<City>();

        // ---------------------------- ******* READ FILE AND CREATE ADJACENCY LIST ******* ----------------------------
        try {
            File fileData = new File("file_data.txt");
            Scanner fileReader = new Scanner(fileData);

            // skip first line
            fileReader.nextLine();
            
            // for each line in the file
            while (fileReader.hasNextLine()) {
                String currentLine = fileReader.nextLine();

                /*  splitLine[0] is city 1
                    splitLine[1] is city 2
                    splitLine[2] is the flight cost
                    splitLine[3] is the flight time
                */
                String[] splitLine = currentLine.split("\\|");

                boolean alreadyExists1 = false;                // used to determine if city 1 is already in the graph
                boolean alreadyExists2 = false;                // used to determine if city 2 is already in the graph
                City duplicateCity1 = new City("NONE");   // used in case a duplicate city is found (initialized to NONE for if no duplicate is found)
                City duplicateCity2 = new City("NONE");   // used in case a duplicate city is found (initialized to NONE for if no duplicate is found)
                
                // loop through adjList
                for (int i = 0; i < adjList.size(); i++) {
                    // if city 1 is found, set alreadyExists1 to True
                    if (adjList.get(i).name.equals(splitLine[0])) {
                        duplicateCity1 = adjList.get(i);
                        alreadyExists1 = true;
                    }
                    // if city 1 is found, set alreadyExists2 to True
                    if (adjList.get(i).name.equals(splitLine[1])) {
                        duplicateCity2 = adjList.get(i);
                        alreadyExists2 = true;
                    }
                }

                // ---------------------------- ******* ADD CITY 1 ******* ----------------------------
                // if city 1 is not already in the adjacency list
                if (!alreadyExists1) {
                    // create new city entry and its corresponding destination
                    City city = new City(splitLine[0]);
                    Destination destination = new Destination(splitLine[1], Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]));

                    // add the new destination to the city's destination list
                    city.destinations.add(destination);
                    adjList.add(city);

                // if city 1 already exists in the adjacency list
                } else {
                    Destination destination = new Destination(splitLine[1], Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]));
                    duplicateCity1.destinations.add(destination);
                    
                }

                // ---------------------------- ******* ADD CITY 2 ******* ----------------------------
                // if city 2 is not already in the adjacency list
                if (!alreadyExists2) {
                    // create new city entry and its corresponding destination
                    City city = new City(splitLine[1]);
                    Destination destination = new Destination(splitLine[0], Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]));

                    // add the new destination to the city's destination list
                    city.destinations.add(destination);
                    adjList.add(city);

                // if city 2 already exists in the adjacency list
                } else {
                    Destination destination = new Destination(splitLine[0], Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]));
                    duplicateCity2.destinations.add(destination);
                }
            }    
            
            fileReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }

        // ---------------------------- ******* READ REQUESTS FILE AND DISPLAY SOLUTIONS ******* ----------------------------

        try {
            File requests = new File("requested_flights.txt");
            Scanner requestsScanner = new Scanner(requests);

            // read first line for number of requests
            int numRequests = Integer.parseInt(requestsScanner.nextLine());

            // for each line in the file
            for (int i = 0; i < numRequests; i++) {
                String line = requestsScanner.nextLine();

                /* splitLine[0] is source
                   splitLine[1] is target
                   splitLine[2] is sorting parameter (cost or time)
                 */
                String[] splitLine = line.split("\\|");

                String source = splitLine[0];
                String target = splitLine[1];
                String sortParameter = splitLine[2];

                // find all solutions and calculate time and cost for each solution
                ArrayList<ArrayList<City>> allSolutions = findAllPaths(source, target, adjList);
                ArrayList<Path> allPaths = calcPathTotals(allSolutions);

                // print the paths
                // if sorting by cost
                if (sortParameter.equals("C"))
                    System.out.println("Flight " + Integer.toString(i + 1) + ": " + source + ", " + target + " (Cost)");
                // if sorting by time
                if (sortParameter.equals("T"))
                    System.out.println("Flight " + Integer.toString(i + 1) + ": " + source + ", " + target + " (Time)");
                
                printSolutions(allPaths, sortParameter);
            }

            requestsScanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    // returns an ArrayList containing all the possible paths between source and target
    public static ArrayList<ArrayList<City>> findAllPaths(String source, String target, LinkedList<City> adjList) {
        ArrayList<City> initialPath = new ArrayList<City>();
        ArrayList<ArrayList<City>> allSolutions = new ArrayList<ArrayList<City>>();

        // find source city in adjList
        City sourceCity = new City("NONE");
        for (int i = 0; i < adjList.size(); i++) {
            if (adjList.get(i).name.equals(source)) {
                sourceCity = adjList.get(i);
            }
        } 

        // define stack for iterative backtracking and push source element with an empty path
        Stack stack = new Stack();
        StackElement sourceElement = new StackElement(sourceCity, initialPath);
        stack.push(sourceElement);

        // iterative backtracking loop, while the stack is not empty
        while (stack.stack.size() > 0) {
            // pop the stack
            StackElement popped = stack.pop();
            City poppedCity = popped.city;
            ArrayList<City> poppedPath = popped.path;
            
            // add the popped element to the current path
            ArrayList<City> updatedPath = new ArrayList<>(poppedPath);
            updatedPath.add(poppedCity);

            // check if target found, if so, add to solutions and backtrack (continue)
            if (poppedCity.name.equals(target)) {
                allSolutions.add(updatedPath);
                continue;
            }   
            
            // if target not found, push all non-visited neighbors onto the stack
            for (int i = 0; i < poppedCity.destinations.size(); i++) {
                boolean visited = false;
                for (int j = 0; j < updatedPath.size(); j++) {
                    if (updatedPath.get(j).name.equals(poppedCity.destinations.get(i).name)) {
                        visited = true;
                    }
                }

                // if not already in the path
                if (!visited) {
                    City nextElement = new City("NONE");
                    for (int k = 0; k < adjList.size(); k++) {
                        if (adjList.get(k).name.equals(poppedCity.destinations.get(i).name)) {
                            nextElement = adjList.get(k);
                        }
                    }

                    // push onto stack
                    ArrayList<City> newPath = new ArrayList<>(updatedPath);
                    StackElement nextNode = new StackElement(nextElement, newPath);
                    stack.push(nextNode);
                }
            }
        }

        return allSolutions;
    }

    // returns an array of path objects, each object contains an array of all cities in the path, the total cost, and the total time
    public static ArrayList<Path> calcPathTotals(ArrayList<ArrayList<City>> allSolutions) {
        ArrayList<Path> allPaths = new ArrayList<Path>();

        // for each path in the solutions list, calculate total cost and time
        for (int i = 0; i < allSolutions.size(); i++) {
            float totalCost = 0;
            int totalTime = 0;
            ArrayList<City> currentArr = allSolutions.get(i);

            for (int j = 0; j <  currentArr.size(); j++) {
                // if not last element
                if (j != (currentArr.size() - 1)) {
                    // find next element in the path and look up its cost and time in the current cities destination list
                    LinkedList<Destination> currentElementDestinations = currentArr.get(j).destinations;
                    City nextElement = currentArr.get(j+1);
                    
                    // loop through current element destinations
                    for (int k = 0; k < currentElementDestinations.size(); k++) {
                        // if destination matches next city in the path, add its cost and time to the totals
                        if (currentElementDestinations.get(k).name.equals(nextElement.name)) {
                            totalCost += currentElementDestinations.get(k).cost;
                            totalTime += currentElementDestinations.get(k).time;
                        }
                    }
                }
            }

            Path pathWithTotals = new Path(currentArr, totalCost, totalTime);
            allPaths.add(pathWithTotals);
        }

        return allPaths;
    }

    // displays all the paths in a paths array
    public static void printSolutions(ArrayList<Path> allPaths, String sortParameter) {
        // if sorting by cost
        if (sortParameter.equals("C")) {
            // sort paths by total cost
            Collections.sort(allPaths, new Comparator<Path>() {
                @Override
                public int compare(Path p1, Path p2) {
                    return Float.compare(p1.totalCost, p2.totalCost);
                }
            });
        }

        // if sorting by time
        if (sortParameter.equals("T")) {
            // sort paths by total time
            Collections.sort(allPaths, new Comparator<Path>() {
                @Override
                public int compare(Path p1, Path p2) {
                    return Integer.compare(p1.totalTime, p2.totalTime);
                }
            });
        }

        // if no solutions found
        if (allPaths.size() == 0) 
            System.out.println("No paths found.");
        
        // print top 3 solutions
        for (int i = 0; i < allPaths.size(); i++) {
            ArrayList<City> currentArr = allPaths.get(i).path;

            // if 3 elements already printed, end
            if (i == 3)
                break;

            System.out.print("Path " + Integer.toString(i + 1) + ": ");

            // for each City in the array
            for (int j = 0; j < currentArr.size(); j++) {
                // if not last element
                if (j != (currentArr.size() - 1)) {
                    System.out.print(currentArr.get(j).name + " -> ");

                // if last element
                } else {
                    System.out.print(currentArr.get(j).name + ".");
                }
            }
            System.out.println(" Time: " + allPaths.get(i).totalTime + " Cost: " + String.format("%.2f", allPaths.get(i).totalCost));
        }
        System.out.println();
    }
} 