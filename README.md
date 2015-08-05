[Jenkins](https://jenkins-ci.org/) plugin to run a [PHP built-in web server](http://php.net/manual/en/features.commandline.webserver.php) for each build.

See https://wiki.jenkins-ci.org/display/JENKINS/PHP+Built-in+Web+Server+Plugin

![alt tag](https://raw.github.com/jenkinsci/php-builtin-web-server-plugin/master/screenshot.png)

#### Compilation

 * `git clone https://github.com/Fengtan/php-builtin-web-server-plugin`
 * `cd php-builtin-web-server-plugin/`
 * `mvn clean install -DskipTests=true`
 
Alternatively, download a pre-compiled .hpi archive from the [releases page] (https://github.com/Fengtan/php-builtin-web-server-plugin/releases)

#### Installation

Assuming Jenkins runs on `http://localhost:8080/`:
 * `wget http://localhost:8080/jnlpJars/jenkins-cli.jar`
 * `java -jar jenkins-cli.jar -s http://localhost:8080/ install-plugin ./target/php-builtin-web-server.hpi -restart`

Alternatively:
 * Go to `http://localhost:8080/pluginManager/advanced`
 * Upload `./target/php-builtin-web-server.hpi`
 * Restart Jenkins

#### Usage

 * The system configuration page allows to set the location of PHP (`php` will be used by default, this can be customized to e.g. `/usr/bin/php`)
 * When configuring a project, a checkbox titled `Run a PHP built-in web server` should show up

#### Dependencies

 * PHP 5.4.0+
