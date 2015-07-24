[Jenkins](https://jenkins-ci.org/) plugin to run a [PHP built-in web server](http://php.net/manual/en/features.commandline.webserver.php) for each build.

TODO screenshot here

#### Compilation
 * `git clone https://github.com/Fengtan/php-builtin-web-server-plugin`
 * `cd php-builtin-web-server-plugin`
 * `mvn clean install -DskipTests=true`

#### Installation
 * Assuming Jenkins runs on `http://localhost:8080/`
 * `wget http://localhost:8080/jnlpJars/jenkins-cli.jar`
 * `java -jar jenkins-cli.jar -s http://localhost:8080/ install-plugin ./target/php-builtin-web-server.hpi -restart`
 * Alternatively go to `http://localhost:8080/pluginManager/advanced`, upload `./target/php-builtin-web-server.hpi` and restart

#### Usage
 * The system configuration page allows to set the location of PHP (`php` will be used by default, this can be customized to e.g. `/usr/bin/php`)
 * When configuring a project, a checkbox titled `Run a PHP built-in web server` should show up

#### Dependencies
 * PHP 5.4.0+
