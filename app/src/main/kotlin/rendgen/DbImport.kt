package rendgen

import org.neo4j.driver.Values

fun insertNode(node: FileNode) {
	println("> file ${node.name}")
	//addFile(node.fileName)
	addModule(node.module)
	node.classes.forEach { classNode ->
		println("> class ${classNode.className}")
		addPackage(classNode.packageNode)
		addModulePackageRelation(node.module, classNode)
		addClass(classNode)
		addPackageClassRelation(classNode)
		classNode.methods.forEach { methodNode ->
			println("> method ${methodNode.methodName}")
			addMethod(methodNode)
			addClassMethodRelation(classNode, methodNode)
			methodNode.calls.forEach { call ->
				println("> call ${call.methodName}")
				addPackage(call.classNode.packageNode)
				addClass(call.classNode)
				addPackageClassRelation(call.classNode)
				addMethod(call)
				addCallRelation(methodNode, call)
			}
		}
	}
}

private fun addModule(module: ModuleNode) {
	GraphDb.executeWriteWithoutResult { tx ->
		tx.run(
			"MERGE (a:Module {name: \$name})",
			Values.parameters("name", module.moduleName)
		)
	}
}

fun addModulePackageRelation(module: ModuleNode, classNode: ClassNode) {
	GraphDb.executeWriteWithoutResult { tx ->
		tx.run(
			"""
	MATCH
	  (m:Module),
	  (p:Package)
	WHERE m.name = '${module.moduleName}' AND p.name = '${classNode.packageNode.packageName}'
	MERGE (m)-[r:HAS]->(p)
""".trimIndent())
	}
}

private fun addPackageClassRelation(classNode: ClassNode) {
	GraphDb.executeWriteWithoutResult { tx ->
		tx.run(
			"""
	MATCH
	  (p:Package),
	  (c:Class)
	WHERE p.name = '${classNode.packageNode.packageName}' AND c.name = '${classNode.className}'
	MERGE (p)-[r:HAS]->(c)
""".trimIndent())
	}
}

fun addClassMethodRelation(classNode: ClassNode, methodNode: MethodNode) {
	GraphDb.executeWriteWithoutResult { tx ->
		tx.run(
			"""
	MATCH
	  (c:Class),
	  (m:Method)
	WHERE c.name = '${classNode.className}' AND m.name = '${methodNode.methodName}'
	MERGE (c)-[r:HAS]->(m)
""".trimIndent())
	}
}

private fun addPackage(packageNode: PackageNode) {
	GraphDb.executeWriteWithoutResult { tx ->
		tx.run(
			"MERGE (a:Package {name: \$name})",
			Values.parameters("name", packageNode.packageName)
		)
	}
}

private fun addClass(classNode: ClassNode) {
	GraphDb.executeWriteWithoutResult { tx ->
		tx.run(
			"MERGE (a:Class {name: \$name})",
			Values.parameters("name", classNode.className)
		)
	}
}

private fun addMethod(methodNode: MethodNode) {
	GraphDb.executeWriteWithoutResult { tx ->
		tx.run(
			"MERGE (a:Method {name: \$name})",
			Values.parameters("name", methodNode.methodName)
		)
	}
}

fun addCallRelation(source: MethodNode, target: MethodNode) {
	GraphDb.executeWriteWithoutResult { tx ->
		tx.run(
			"""
	MATCH
	  (s:Method),
	  (t:Method)
	WHERE s.name = '${source.methodName}' AND t.name = '${target.methodName}'
	MERGE (s)-[r:USE]->(t)
""".trimIndent()
		)
	}
}
