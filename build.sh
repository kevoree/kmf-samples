export JAVA_HOME=$(/usr/libexec/java_home)
export MAVEN_OPTS="-Xmx3000m -XX:MaxPermSize=256m"
mvn clean install