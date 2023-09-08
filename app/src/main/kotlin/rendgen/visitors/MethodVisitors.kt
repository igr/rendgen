package rendgen.visitors

import com.github.javaparser.ast.AccessSpecifier
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import rendgen.*

fun methodVisitor(project: Project, classNodes: List<ClassNode>) = object : VoidVisitorAdapter<Any>() {
	override fun visit(method: MethodDeclaration, arg: Any?) {
		super.visit(method, arg)

		// visit all methods
		//if (method.isPrivate) return

		// todo this should not happened
		if (classNodes.isEmpty()) return

		val classNode = classNodes.last()
		val methodNodeName = "${classNode.className}#${method.nameAsString}"
		val methodNode = MethodNode(classNode, methodNodeName, method.isPublic)
		classNode.methods.add(methodNode)
		println("   + ${methodNode.methodName}")

		method.findAll(ObjectCreationExpr::class.java).forEach { safeExecute(it) { oce ->
			val o = oce.resolve()
			if (o.packageName.startsWith(project.packagePrefix)) {
				val callClassNode = ClassNode(PackageNode(o.packageName), "${o.packageName}.${o.className}")
				val callMethodNodeName = "${callClassNode.className}#<init>"
				val calledMethod = MethodNode(callClassNode, callMethodNodeName, true)
				methodNode.calls.add(calledMethod)
				println("     - ${calledMethod.methodName}")
			}
		}}

		method.findAll(MethodCallExpr::class.java).forEach { safeExecute(it) { mce ->
			val m = mce.resolve()
			if (
				// we don't want to capture calls to private methods
				m.accessSpecifier() != AccessSpecifier.PRIVATE &&
				m.packageName.startsWith(project.packagePrefix)
			) {
				val callClassNode = ClassNode(PackageNode(m.packageName), "${m.packageName}.${m.className}")
				val callMethodNodeName = "${callClassNode.className}#${m.name}"
				val calledMethod = MethodNode(callClassNode, callMethodNodeName, m.accessSpecifier() == AccessSpecifier.PUBLIC)
				methodNode.calls.add(calledMethod)
				println("     - ${calledMethod.methodName}")
			}
		}}
	}
}