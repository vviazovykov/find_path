package com.somecompany.app;

import com.somecompany.app.core.PathFinder;

public class FindPathRunner
{
    public static void main( String[] args )
    {
        String path = "D:\\TEST_3\\find_path\\src\\main\\resourses\\inputGrid.txt";
        PathFinder pathFinder = new PathFinder(path);

        pathFinder.findPath();

        System.out.print("Input:");
        pathFinder.printMaze();

        System.out.println("Ouput:\n" + pathFinder.getOutputPath());
    }
}
