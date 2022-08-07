subprojects {
    group = properties["group"]!!
    version = properties["mod_version"]!!

    tasks.withType<Jar> {
        from("LICENSE")
    }
}