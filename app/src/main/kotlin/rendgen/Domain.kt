package rendgen

import java.io.File

@JvmInline
value class ProjectName(private val value: String)
@JvmInline
value class ProjectRoot(private val value: String) {
	fun file() = File(value)

	fun relativePathOf(file: File) = file.absolutePath.removePrefix(value)
}

data class Project(
	val name: ProjectName,
	val packagePrefix: String,
	val root: ProjectRoot,
	val jars: List<String>,
)

data class ScannedProject(
	val project: Project,
	val files: List<FileNode>,
)

data class MethodNode(
	val classNode: ClassNode,
	val methodName: String,
	val public: Boolean,
	val calls: MutableList<MethodNode> = mutableListOf(),
)

data class ClassNode(val packageNode: PackageNode,
                     val className: String,
                     val methods: MutableList<MethodNode> = mutableListOf(),
) {
	companion object {
		fun of(fullQualifiedName: String): ClassNode = ClassNode(
			packageNode = PackageNode(fullQualifiedName.substringBeforeLast(".")),
			className = fullQualifiedName,
		)
	}
}

data class FileNode(
	val name: String,
	val fileName: String,
	val classes: List<ClassNode>,
	val module: ModuleNode,
)

data class PackageNode(
	val packageName: String,
)

data class ModuleNode(
	val moduleName: String,
)
