package rendgen.visitors

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import rendgen.ClassNode

fun classVisitor(classNodes: MutableList<ClassNode>) = object : VoidVisitorAdapter<Any>() {
	override fun visit(declaration: ClassOrInterfaceDeclaration, arg: Any?) {
		super.visit(declaration, arg)
		if (declaration.fullyQualifiedName.isPresent) {
			val classNode = ClassNode.of(fullQualifiedName = declaration.fullyQualifiedName.get())
			classNodes.add(classNode)
			println(" * ${classNode.className}")
		}
	}
}