package rendgen

fun main() {
    val project = Project(
        name = ProjectName("qlue"),
        packagePrefix = "com.webkreator.qlue.",
        root = ProjectRoot("/Users/igors/prj/red/qlue"),
        jars = listOf("build/libs/qlue-3.1.3.jar"),
    )

    val scannedProject = scanJavaProject(project)

    println("Scanned ${scannedProject.files.size} files.")

    GraphDb.use {
        scannedProject.files.forEach { node -> insertNode(node) }
    }
}
