# Doppelgänger
An interactive tool to eliminate code clones in Java applications


## Features

- Analyze any Java application and detect its duplicated source code
- Either input a local project or any that is available via a public Git repository
- Receive an advanced visualization of your code clones
- Explore your application and its code clones to interactively eliminate them


## Idea

As part of a course in university, [Núria](https://github.com/NuriaBruchTarrega) and me had to do a clone detection application.
We very much enjoyed this assignment and eventually built it into this application.
While the core of _Doppelgänger_ is using AST parsing and hashing to detect source code clones,
the output can be explored visually:<br><br>
![screenshot-1](https://i.imgur.com/E1FMHvy.png)


## Running

The tool consists of a back-end written in Kotlin as well as front-end built with Vue.js.
The entirety of the application is bundled into Docker images and can be executed with `docker-compose`.
After having it installed, while in the root project directory, just run `docker-compose up -d`.


## Usage

### Analysis
1) Select if you want to supply a local, or a Git-hosted project via the first dropdown input.
2) Either put in a path on your local file system or a Git repository URI into the second input.
    - Local project example: `/home/username/projects/my-app/`
    - Git project example: `git@github.com:username/my-app.git`
3) Optionally specify the base path to your source code files in the third input.
    - Example: `/src/main/java/`
4) Select the clone type to use for the analysis.
    - Type One: _exact copy, ignoring whitespace and comments_
    - Type Two: _syntical copy, changes allowed in variable, type, and function identifiers_
    - Type Three: _copy with changed, added, or deleted statements_
5) Choose a _similarity threshold_ for the analysis.
    - This value determines how similar source code fragments have to be, to be flagged as clones.
6) Choose a _mass threshold_ for the analysis.
    - This value determines how precise the analysis will be. Generally speaking, a low value will improve accuracy while decreasing the speed.
7) Click the `Analyze` button and start exploring the detected clones.

### Exploration
_Follows soon_


## License

This project is licensed under the [MIT license](LICENSE).
