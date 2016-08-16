# spark-heroku
Sample app for Spark Java framework and Gradle on Heroku.

Test locally:

```
./gradlew stage
heroku local web
```

Check it out at `http://localhost:5000/hello`

Create the git repository and commit the changes:

```
git add .
git commit -m "Initial commit"
```

Deploy:

```
git push heroku master
```

Open:

```
heroku open
```
