plugins {
    java
    application
}

application.mainClass.set("up.visulog.cli.CliApplication")

dependencies {
    implementation(project(":analyzer"))
    implementation(project(":config"))
    implementation(project(":gitrawdata"))
    testImplementation("junit:junit:4.+")
}


