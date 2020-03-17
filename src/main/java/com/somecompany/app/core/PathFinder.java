package com.somecompany.app.core;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PathFinder {

    private static final Logger LOG = Logger.getLogger(PathFinder.class.getSimpleName());
    // Path to the file with maze
    private String path;
    // List of the whole maze filled with String values from the resource file
    private List<ArrayList<String>> maze;
    // Map which contains Start and Finish position
    private Map<String, Integer> mapWithStartFinishPositions;
    // Keeps route from Start to Finish positions
    private List<Character> outputPath;

    private int columnOfX;

    public PathFinder(String path) {
        this.path = path;
        this.maze = new ArrayList<ArrayList<String>>();
        this.mapWithStartFinishPositions = new HashMap<>();
        this.outputPath = new ArrayList<>();
    }

    public List<Character> getOutputPath() {
        return outputPath;
    }

    public void findPath() {
        try {
            fillMazeFromFile();
        } catch (FileNotFoundException e) {
            LOG.error("Cannot find the file", e);
        }

        fillMapWithStartFinishPositions();

        if (findShortestPath(mapWithStartFinishPositions.get("startLine"), mapWithStartFinishPositions.get("startColumn"))) {
            System.out.println("The path is found");
            outputPath = reverseOutputPathElements();
        } else {
            System.out.println("The path is not found");
        }
    }

    /**
     * Method reads the file and save their symbols in Arraylist of a String Arraylist
     *
     * @throws FileNotFoundException if file is not found
     */
    private void fillMazeFromFile() throws FileNotFoundException {

        Scanner scan = new Scanner(new File(path));

        // adding "-" or "|" around maze to avoid ArrayIndexOutOfBoundsException
        String currentLine = scan.nextLine();

        maze.add(new ArrayList<String>());
        // "-" in the first line
        for (int i = 0; i<currentLine.length() + 2; i++) {
            maze.get(0).add("-");
        }

        maze.add(new ArrayList<String>(Arrays.asList(currentLine.split(""))));
        maze.get(1).add(0,"|");
        maze.get(1).add(maze.get(1).size(),"|");
        int index=2;

        // "|" at the start and in the end of the loaded line
        while (scan.hasNextLine()) {
            currentLine = scan.nextLine();
            maze.add(new ArrayList<String>(Arrays.asList(currentLine.split(""))));
            maze.get(index).add(0,"|");
            maze.get(index).add(maze.get(index).size(),"|");
            // find index of the Finish column position
            if (maze.get(index).contains("X")) {
                columnOfX = maze.get(index).indexOf("X") + 1;
            }
            index++;
        }
        // "-" in the last line
        maze.add((ArrayList<String>) Stream.generate(() -> "-")
                .limit(maze.get(index - 1).size())
                .collect(Collectors.toList()));
    }

    private void fillMapWithStartFinishPositions() {
        //this method is trying to found starting and finishing locations
        mapWithStartFinishPositions.put("startLine",-1);
        mapWithStartFinishPositions.put("finishLine", -1);

        for (int i = 0; i< maze.size(); i++) {
            if (maze.get(i).contains("S")) {
                mapWithStartFinishPositions.put("startLine", i);
                mapWithStartFinishPositions.put("startColumn", maze.get(i).indexOf("S"));
            }
            if (maze.get(i).contains("X")) {
                mapWithStartFinishPositions.put("finishLine", i);
                mapWithStartFinishPositions.put("finishColumn", maze.get(i).indexOf("X"));
            }
        }
        // if there is no Starting position in the maze, there will be default one at the [1,1}
        if (mapWithStartFinishPositions.get("startLine") == (-1)) {
            mapWithStartFinishPositions.put("startLine",1);
            mapWithStartFinishPositions.put("startColumn",1);
            maze.get(1).set(1, "S");
        }
        // if there is no Finish position in the maze, there will be default one at the [size-2, size-2]
        if (mapWithStartFinishPositions.get("finishLine") == (-1)) {
            mapWithStartFinishPositions.put("finishLine",maze.size() - 2);
            mapWithStartFinishPositions.put("finishColumn",maze.get(maze.size() - 1).size() - 2);
            maze.get(maze.size() - 2).set(maze.get(maze.size() - 2).size() - 2, "X");
        }
    }

    /**
     * Method for printing maze to the console.
     */
    public void printMaze(){
        // adding S to the started location so we can see where we started
        maze.get(mapWithStartFinishPositions.get("startLine")).set(mapWithStartFinishPositions.get("startColumn"), "S");

        for (List<String> line : maze) {
            System.out.println();
            for (String column : line) {
                System.out.print(column);
            }
        }
        System.out.println();
    }

    private boolean  findShortestPath (int row, int column) {

        // method should recursivly iterate until there is full maze of 1 (visited) which means there is no path
        // or we found end (x)
        //
        // dr,dc stands for RowDiff nad ColumnDiff
        int dr,dc;

        //return true if we found the end
        if (maze.get(row).get(column).equals("X")) {
            return true;
        }

        // go further until find Finish position
        if ((maze.get(row).get(column).equals("S")) || (maze.get(row).get(column).equals("."))) {

            if (columnOfX == column) {
                return false;
            }
            maze.get(row).set(column, "1");
            //down
            dr = +1;
            dc = 0;
            if (findShortestPath(row + dr, column + dc)) {
                outputPath.add('d');
                return true;
            }
            //right
            dr = 0;
            dc = 1;
            if (findShortestPath(row + dr, column + dc)) {
                outputPath.add('r');
                return true;
            }
            //up
            dr = -1;
            dc = 0;
            if (findShortestPath(row + dr, column + dc)) {
                outputPath.add('u');
                return true;
            }
            //left
            dr = 0;
            dc = -1;
            if (findShortestPath(row + dr, column + dc)) {
                outputPath.add('l');
                return true;
            }
        }
        return false;
    }

    private List<Character> reverseOutputPathElements() {
        final int lastElement = outputPath.size() - 1;
        return IntStream.rangeClosed(0, lastElement)
                .map(i -> (lastElement - i))
                .mapToObj(outputPath::get)
                .collect(Collectors.toList());
    }
}
