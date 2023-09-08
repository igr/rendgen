package rendgen

import com.github.javaparser.ParserConfiguration
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import com.github.javaparser.utils.SourceRoot
import rendgen.visitors.classVisitor
import rendgen.visitors.methodVisitor
import java.io.File
import java.nio.file.Path

fun scanJavaProject(project: Project) = project.root.file()
	.walkTopDown()
	.filter { it.isFile }
	.filter { it.name.endsWith(".java") }
	.filter { !it.absolutePath.contains("/src/test/") }
	.map { makeFileNode(project, it) }
	.toList()
	.let { ScannedProject(project, it) }

private fun makeFileNode(project: Project, file: File): FileNode {
	val root = project.root
	val moduleRoot = file.absolutePath.substringBefore("/src/")

	val classes = parseSourceFile(
		project, moduleRoot, root.relativePathOf(file).substringAfter("/"))

	return FileNode(
		name = file.name,
		fileName = root.relativePathOf(file),
		classes = classes,
		module = resolveModule(file),
	)
}

private fun resolveModule(file: File): ModuleNode {
	return ModuleNode(file.absolutePath.substringBefore("/src/").substringAfterLast("/"))
}

private fun parseSourceFile(project: Project, moduleRoot: String, sourceFile: String): List<ClassNode> {
	val typeSolver = CombinedTypeSolver(
		ReflectionTypeSolver(),
		JavaParserTypeSolver(File("$moduleRoot/src/main/java")),
		//JavaParserTypeSolver(File("generated_code"))
	)
	project.jars.forEach {
		typeSolver.add(JarTypeSolver(File("$moduleRoot/$it")))
	}

	val symbolSolver = JavaSymbolSolver(typeSolver)

	val pc = ParserConfiguration().setSymbolResolver(symbolSolver)
	val sourceRoot = SourceRoot(Path.of(moduleRoot), pc)
	val cu = sourceRoot.parse("", sourceFile)

	val classNodes = mutableListOf<ClassNode>()

	cu.accept(classVisitor(classNodes), null)
	cu.accept(methodVisitor(project, classNodes), null)

	return classNodes
}
