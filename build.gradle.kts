group = "me.lunaluna"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {

    group = "${properties["group"]}"
    version = "${properties["mod_version"]}"

    tasks.withType<Jar> {
        from("LICENSE")
    }
}