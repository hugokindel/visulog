# visulog

![visulog](https://hk-backup.s3.eu-west-3.amazonaws.com/images/Untitled.png)

visulog is a tool for analysis and visualization of git logs which aims to provides an interesting and personalized result for any git analysis by providing an HTML page as output.

## Features

- Multiple command line options.
- Compute a couple of relevant indicators for a specific repository, such as:
    - Number of commits per author/mail.
    - Number of lines added/deleted per author/mail.
    - Number of files changed per author/mail.
    - Number of merge commits.
    - List of branches.
    - Number of contributors.
    - Type and data of the project progression.
    - Curve of the project progression.
- Possibility to filter the result:
    - By branch.
    - By start date.
    - By end date.
    - By author whitelist.
    - By author blacklist.
- Graphical interface written in PHP.

## Architecture

visulog contains the following modules:

- **analyzer** - Main library containing the code of every plugins.
- **cli** - The executable containing the command line interface.
- **config** - Utility library with configuration classes.
- **gitrawdata** - Data format related to git (uses JGit).
- **webgen** - Generator of web pages and graphs (uses CanvasJS and HtmlFlow).
- **webgui** - Code to use with a web server to access a web graphical interface which can facilitate the use of the cli commands.

## How to use

### From sources

First of all, you need to clone the repository by using `git clone git@gaufre.informatique.univ-paris-diderot.fr:hugokindel/visulog.git` or `git clone https://gaufre.informatique.univ-paris-diderot.fr/hugokindel/visulog.git`

#### Compile with an IDE (if it supports Gradle):

1) Open the project's directory in your IDE.
2) Use the `run` gradle task.

#### Compile with the command line:

1) Open a terminal in your project directory.  
1.1. **Only if you are on a SCRIPT computer**, run `source SCRIPT/envsetup`.
This will setup the GRADLE_OPTS environment variable so that gradle uses the SCRIPT proxy for downloading its dependencies. It will also use a custom trust store (the one installed in the system is apparently broken... ).
2) Run gradle wrapper (it will download all dependencies, including gradle itself) `./gradlew build`.
3) You can finally run the software with `./gradlew run` (of course, it won't do anything if you don't specify any arguments).

### From a binary distribution

If you got a binary release (containing all the libs and a bin directory), you can directly use `./cli` (for UNIX systems) or `./cli.bat` (for Windows).

## How to use the webgui

1) Install a web server that supports PHP (5.6+).
2) Make sure to have visulog compiled as a binary distribution and that the bin directory is within your path variable.
3) Copy the webgui directory to your web server `www` directory.
4) Go to the URL of visulog's webgui (should be `localhost/webgui` by default).

## Examples

For most of the examples, we are going to imagine that we are using a binary distribution under Linux and that we've added its bin directory path into our path variable and that out working directory is the repository.

**To show the help:**  
`cli -h`

**To show the version:**  
`cli -v`

**To use all plugins:**  
Take the list resulted by `cli --list-plugins` and pass it to `cli --plugins=the-resulted-list`.

**To specify a start and/or end date (meaning only commits within these dates will be analyzed):**  
`cli --plugins=CountCommitsPerAuthor --start=01/12/2020 --end=31/12/2020`

**To specify a branch to work (meaning only commits within this branch will be analyzed):**  
`cli --plugins=CountCommitsPerAuthor --branch=--branch=refs/remotes/origin/name-of-branch-on-the-origin`

**To specify authors in the whitelist (meaning only commits whitelisted authors will be analyzed):**  
`cli --plugins=CountCommitsPerAuthor --whitelist=adegorre@irif.fr,colin@nomadic-labs.com`

**To specify authors in the blacklist (meaning they will be hidden from the analyze):**  
`cli --plugins=CountCommitsPerAuthor --blacklist=adegorre@irif.fr,colin@nomadic-labs.com`

**To use aliases for authors (meaning that each time we find a commit made by the mail from an alias, the username and first mail specified in the alias will be used as this person's identity, useful for people with multiple mails or multiple git names):**  
`cli --plugins=CountCommitsPerAuthor --alias=MOHAMED_CASSIM_Tasnim,colataka@hotmail.fr --alias=ALLANO_Yoann,ylrallano@gmail.com --alias=SOMON_Bastian,bastian.somon@gmail.com --alias=JAUROYON_Maxime,maxime.jauroyon@gmail.com --alias=PAYET_Jylan,jylanpayet@gmail.com --alias=CLIPAL_Moise,elymo.mc@gmail.com --alias=DEGORRE_Aldric,adegorre@irif.fr --alias=GONZALEZ_Colin,colin@nomadic-labs.com`  
PS: Spaces in names must be declared with `_`.

**To save a configuration (it will create a file containing the command you wrote):**  
`cli --save=save.txt --plugins=CountCommitsPerAuthor --alias=MOHAMED_CASSIM_Tasnim,colataka@hotmail.fr --alias=ALLANO_Yoann,ylrallano@gmail.com --alias=SOMON_Bastian,bastian.somon@gmail.com --alias=JAUROYON_Maxime,maxime.jauroyon@gmail.com --alias=PAYET_Jylan,jylanpayet@gmail.com --alias=CLIPAL_Moise,elymo.mc@gmail.com --alias=DEGORRE_Aldric,adegorre@irif.fr --alias=GONZALEZ_Colin,colin@nomadic-labs.com --blacklist=adegorre@irif.fr,colin@nomadic-labs.com`

**To load a configuration previously saved:**  
`cli --load=save.txt`

**To change the title of the generated page:**  
`cli --title=New_title --plugins=CountCommitsPerAuthor`  
PS: Spaces in titles must be declared with `_`.

**To change the css of the generated page (it will append the path of your css to the generated page):**  
`cli --css=custom.css --plugins=CountCommitsPerAuthor`

**To change the format of the date for the graphs and start/end options):**  
`cli --format=MM/dd/yyyy --plugins=TypeOfProgression,ProjectProgression`

## Similar tools

- [gitstats](https://pypi.org/project/gitstats/)

## Third party libraries

- [CanvasJS](https://canvasjs.com/)
- [HtmlFlow](https://htmlflow.org/)
- [JGit](https://www.eclipse.org/jgit/)

## Contributors

- [SOMON Bastian](https://gaufre.informatique.univ-paris-diderot.fr/somon)
- [JAUROYON Maxime](https://gaufre.informatique.univ-paris-diderot.fr/jauroyon)
- [CLIPAL Moïse](https://gaufre.informatique.univ-paris-diderot.fr/clipal)
- [KINDEL Hugo](https://gaufre.informatique.univ-paris-diderot.fr/hugokindel)
- [PAYET Jylan](https://gaufre.informatique.univ-paris-diderot.fr/payetj)
- [MOHAMED CASSIM Tasnim](https://gaufre.informatique.univ-paris-diderot.fr/mohamedc)
- [ALLANO Yoann](https://gaufre.informatique.univ-paris-diderot.fr/allano)

## License

This project is made for educational purposes only.
Forked from [visulog](https://gaufre.informatique.univ-paris-diderot.fr/adegorre/visulog) by [Aldric Degorre](https://gaufre.informatique.univ-paris-diderot.fr/adegorre)