package controller.analysis.parsing

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import model.Unit
import utility.toNullable
import java.io.File
import java.nio.file.Paths


class Parser(private val projectRoot: File) {
    fun parse(): List<Unit> {
        val parser: JavaParser = constructJavaParser()
        val visitor = Visitor()
        return projectRoot
            .walk()
            .filter { !it.isDirectory }
            .mapNotNull { parser.parse(it).result.toNullable() }
            .map { visitor.visit(it) }
            .flatten()
            .toList()
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
}
