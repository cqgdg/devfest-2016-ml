##### Run with maven

```
mvn clean spring-boot:run \
    -DWX_APP_ID=\
    -DWX_APP_SECRET=\
    -DQN_BACKET=\
    -DQN_ACCESS_KEY=\
    -DQN_SECRET_KEY=\
    -DQN_HOST=\
    -DGOOGLE_KEY=
```
##### Run with docker

```
docker run -i -t -d --restart=always --name devfest-2016-ml \
    -p 80:8080 \
    -e WX_APP_ID=\
    -e WX_APP_SECRET=\
    -e QN_BACKET=\
    -e QN_ACCESS_KEY=\
    -e QN_SECRET_KEY=\
    -e QN_HOST=\
    -e GOOGLE_KEY=\
    cqgdg/devfest-2016-ml
```
