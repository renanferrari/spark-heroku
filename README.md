# spark-heroku
Sample app for [Spark Java framework](https://sparkjava.com) and [Gradle](https://gradle.org) on [Heroku](https://heroku.com).


## Usage

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


## References

https://devcenter.heroku.com/articles/getting-started-with-gradle-on-heroku
https://github.com/heroku/gradle-getting-started
