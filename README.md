# EW Tools

Tools for exporting data from an Easy Worship database

Only tested on EW 6.1

## Build

    ./gradlew build
    
Output is in `build/libs`

## Usage

    java -jar EW-tools.jar <path> <command>
    
`<path>` is the path to the database of Easy Worship (the directory which contains `Resources` and `v6.1`)

`<command>` is the command which should be executed. Currently only `export` is possible. 
This exports all songs as plain text to `./export`