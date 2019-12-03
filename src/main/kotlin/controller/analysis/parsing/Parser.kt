package controller.analysis.parsing

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.Node
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import model.CloneType
import model.Unit
import utility.toNullable
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass


class Parser(private val projectRoot: File, private val cloneType: CloneType) {
    fun parse(): List<Unit> {
        val parser = constructJavaParser()
        val nodeConversionFunction = constructNodeConversionFunction()

        return projectRoot
            .walk()
            .filter { it.isFile }
            .mapNotNull { parser.parse(it).result.toNullable() }
            .map { Visitor.visit(it, nodeConversionFunction) }
            .flatten()
            .toList()
    }

    private fun filterOutNodeTypes(node: Node, vararg types: KClass<out Node>): Node {
        node.childNodes.removeAll { types.contains(it::class) }
        node.childNodes.forEach { filterOutNodeTypes(it, *types) }
        return node
    }

    private fun constructJavaParser(): JavaParser {
        val symbolSolver = JavaSymbolSolver(constructTypeSolver(sourceCodeLocation = projectRoot))
        val configuration: ParserConfiguration = ParserConfiguration().setSymbolResolver(symbolSolver)
        return JavaParser(configuration)
    }

    private fun constructTypeSolver(sourceCodeLocation: File? = null, compiledCodeLocation: File? = null): TypeSolver {
        require(!(sourceCodeLocation == null && compiledCodeLocation == null)) { "At least one input location is mandatory" }

        val combinedTypeSolver = CombinedTypeSolver()
        if (sourceCodeLocation != null) combinedTypeSolver.add(JavaParserTypeSolver(sourceCodeLocation.absolutePath))
        if (compiledCodeLocation != null) combinedTypeSolver.add(JarTypeSolver(Paths.get(compiledCodeLocation.absolutePath)))
        return combinedTypeSolver
    }

    private fun constructNodeConversionFunction(): (Node) -> Unit {
        return { node: Node -> Unit.fromNode(node = node, cloneType = cloneType) }
    }
}
