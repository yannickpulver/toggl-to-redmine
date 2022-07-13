# Toggl-2-Redmine

Handy Kotlin Console application that transfers your Redmine projects to Toggl and your time entries from Toggl to
Redmine.

### 🪄 Features / Commands:

`t2r fetchProjects` - Adds projects from Redmine as projects in Toggl

`t2r addTime DATE` - Adds time entries from Toggl to Redmine. Date format: `2022-06-24`

### 🧑‍💻 Usage

Run `t2r fetchProjects` to get all the projects from Redmine to Toggl. When doing time entries, select one of these
projects. You may add `tags` with your Redmine Activity name, otherwise the default activity will be taken (you will get
asked to choose one.).

If you want to report time not by project but by issue, add the issue number in front of the description
like: `12345 something`.

In order to submit time entries, you can call `t2r addTime 2022-07-12` (replace the date with the date you want to
submit).



### 🚧 How to install:

1. Take the latest `toggl2redmine.jar` from [Releases](/releases) and place it on your Desktop (mac):
2. Open Terminal
3. Copy Paste following block into Terminal + Click Enter:

```
mkdir -p ~/Toggl && mv ~/Desktop/toggl2redmine.jar ~/Toggl/toggl2redmine.jar
echo "alias t2r='java -jar ~/Toggl/toggl2redmine.jar'" >> ~/.zshrc
source ~/.zshrc
```

4. Now you should be able to run `t2r`, `t2r fetchProjects` and `t2r addTime DATE`.


### ⚠️ Java Error during execution?

Then you need to do a few extra steps to install java. 
1. Install homebrew (skip if already have it):

```
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

2. Run these commands.

```
brew install java
echo 'export PATH="/opt/homebrew/opt/openjdk/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
   ```

Now `t2r` should be available. 