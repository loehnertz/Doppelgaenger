package controller.analysis.parsing

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.Node
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import kotlinx.coroutines.runBlocking
import model.CloneType
import model.Unit
import utility.mapConcurrently
import utility.toNullable
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass


class Parser(private val basePath: String, private val projectRoot: File, private val cloneType: CloneType) {
    fun parse(): List<Unit> = runBlocking {
        val parser: JavaParser = constructJavaParser()
        val nodeConversionFunction: (Node) -> Unit = constructNodeConversionFunction()

        return@runBlocking projectRoot
            .walk()
            .filter { it.isFile }
            .filter { it.extension == JavaFileExtension }
            .toList()
            .mapNotNull { parser.parse(it).result.toNullable() }
            .mapConcurrently { Visitor.visit(it, nodeConversionFunction) }
            .flatten()
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
        return { node: Node -> Unit.fromNode(node = node, basePath = basePath, cloneType = cloneType) }
    }

    companion object Constants {
        private const val JavaFileExtension = "java"
    }
}
